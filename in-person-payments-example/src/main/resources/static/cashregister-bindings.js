var abortController;

// Sends POST request to url
async function sendPostRequest(url, data) {
    abortController = new AbortController(); // Used for cancelling the request
    const res = await fetch(url, {
        method: "POST",
        body: data ? JSON.stringify(data) : "",
        headers: {
            "Content-Type": "application/json",
            "Connection": "keep-alive",
            "Keep-Alive": "timeout=180, max=180"
        },
        signal: abortController.signal
    });

    return await res.json();
}

// Sends GET request to URL
async function sendGetRequest(url) {
    const res = await fetch(url, {
        method: "Get",
        headers: {
            "Content-Type": "application/json",
            "Connection": "keep-alive",
            "Keep-Alive": "timeout=180, max=180"
        }
    });

    return await res.json();
}

// Sends abort request to cancel an on-going transaction for the table
async function sendAbortRequest(tableName) {
    try {
        var response = await sendGetRequest("/api/abort/" + tableName);
    }
    catch(error) {
        console.warn(error);
    }
}

// Shows loading animation component and deactivates the table selection
function showLoadingComponent() {
    document.getElementById('loading-grid').classList.remove('disabled');
    document.getElementById('tables-section').classList.add('disabled');
}

// Hides loading animation component and shows table selection selection
function hideLoadingComponent() {
    document.getElementById('loading-grid').classList.add('disabled');
    document.getElementById('tables-section').classList.remove('disabled');
}

// Bind table selection buttons and the `pay/reversal/transaction-status` submit-buttons
function bindButtons() {
    // Bind `payment-request-form` submit-button
    const paymentRequestForm = document.getElementById('payment-request-form');
    paymentRequestForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        var formData = new FormData(event.target);
        var amount = formData.get('amount');
        var currency = formData.get('currency');
        var tableName = formData.get('tableName');

        if (amount && currency && tableName) {
            try {
                // Show loading animation component which doesn't allow users to select any tables
                showLoadingComponent();

                // Send payment request
                var response = await sendPostRequest("/api/create-payment", { tableName: tableName, amount: amount, currency: currency });
                console.log(response);

                // Handle response
                switch (response.result) {
                    case "success":
                        window.location.href = "result/success";
                        break;
                    case "failure":
                        window.location.href = "result/failure/" + response.refusalReason;
                        break;
                    default:
                        throw Error('Unknown response result');
                }
            }
            catch (error) {
                console.warn(error);

                // Sends an abort request to the terminal
                await sendAbortRequest(tableName);

                // Hides loading animation component and allow user to select tables again
                hideLoadingComponent();
            }
        }
    });

    // Bind `reversal-request-form` submit-button
    const reversalForm = document.getElementById('reversal-request-form');
    reversalForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        var formData = new FormData(event.target);
        var reversalTableName = formData.get('reversalTableName');

        if (reversalTableName) {
            try {
                // Show loading animation component and don't allow user to select any tables
                showLoadingComponent();

                // Send reversal request
                var response = await sendPostRequest("/api/create-reversal", { tableName: reversalTableName });
                console.log(response);

                // Handle response
                switch (response.result) {
                    case "success":
                        window.location.href = "result/success";
                        break;
                    case "failure":
                        window.location.href = "result/failure/" + response.refusalReason;
                        break;
                    default:
                        throw Error('Unknown response result');
                }
            }
            catch (error) {
                console.warn(error);

                // Hides loading animation component and allow user to select tables again
                hideLoadingComponent();
            }
        }
    });

    // Bind `cancel-operation-button`
    const cancelOperationButton = document.getElementById('cancel-operation-button');
    cancelOperationButton.addEventListener('click', () => {
        // Abort sending post request
        abortController.abort();

        // Hide loading animation component
        hideLoadingComponent();
    });

    // Bind `transaction-status-button`
    const transactionStatusButton = document.getElementById('transaction-status-button');
    transactionStatusButton.addEventListener('click', async () => {
        const tableNameElement = document.getElementById('tableName');
        if (!tableNameElement.value) {
            return;
        }

        // Go to transaction status page for the given tableNameElement.value 
        window.location.href = "transaction-status/" + tableNameElement.value;
    });

    // Allows user to select a table by binding all tables to a click event
    const tables = document.querySelectorAll('.tables-grid-item');
    tables.forEach(table => {
        table.addEventListener('click', function() {
            // Remove the 'current-selection' class from all `table-grid-items`
            tables.forEach(item => item.classList.remove('current-selection'));

            // Add the 'current-selection' class to the currently selected `tables-grid-item`
            table.classList.add('current-selection');

            // Copies 'amount' value to the `payment-request-form`
            const amountElement = document.getElementById('amount');
            amountElement.value = table.querySelector('.tables-grid-item-amount').textContent;

            // Copies 'currency' value to the `payment-request-form`
            const currencyElement = document.getElementById('currency');
            currencyElement.value = table.querySelector('.tables-grid-item-currency').textContent;

            // Copies 'table name' value to the `payment-request-form`
            const tableNameElement = document.getElementById('tableName');
            tableNameElement.value = table.querySelector('.tables-grid-item-title').textContent;

            // Copies 'table name' value to the `reversal-request-form`
            const reversalTableNameElement = document.getElementById('reversalTableName');
            reversalTableNameElement.value = table.querySelector('.tables-grid-item-title').textContent;

            // Show/hides the `payment-request-button` and `reversal-request-button` according to the `PaymentStatus` of currently selected table
            const currentActiveTable = document.getElementsByClassName('current-selection')[0];
            var statusValue = currentActiveTable.querySelector('.tables-grid-item-status').textContent;
            switch (statusValue) {
                case 'NotPaid':
                    enablePaymentRequestButton();
                    disableReversalRequestButton();
                    disableTransactionStatusButton();
                    break;
                case 'Paid':
                    disablePaymentRequestButton();
                    enableReversalRequestButton();
                    enableTransactionStatusButton();
                    break;
                case 'RefundFailed':
                    disablePaymentRequestButton();
                    enableReversalRequestButton();
                    enableTransactionStatusButton();
                    break;
                case 'RefundInProgress':
                case 'PaymentInProgress':
                case 'Refunded':
                case 'RefundedReversed':
                default:
                    disablePaymentRequestButton();
                    disableReversalRequestButton();
                    enableTransactionStatusButton();
                    break;
            }
        });
    });
}

// Enable `payment-request-button`
function enablePaymentRequestButton() {
    document.getElementById('payment-request-button').classList.remove('disabled');
}

// Disable `payment-request-button`
function disablePaymentRequestButton() {
    document.getElementById('payment-request-button').classList.add('disabled');
}

// Enable `reversal-request-button`
function enableReversalRequestButton() {
    document.getElementById('reversal-request-button').classList.remove('disabled');
}

// Disable `reversal-request-button`
function disableReversalRequestButton() {
    document.getElementById('reversal-request-button').classList.add('disabled');
}

// Enable `transaction-status-button`
function enableTransactionStatusButton() {
    document.getElementById('transaction-status-button').classList.remove('disabled');
}

// Disable `transaction-status-button`
function disableTransactionStatusButton() {
    document.getElementById('transaction-status-button').classList.add('disabled');
}

bindButtons();