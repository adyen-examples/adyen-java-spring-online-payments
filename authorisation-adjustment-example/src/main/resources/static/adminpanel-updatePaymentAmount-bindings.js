// Sends POST request to url
async function sendPostRequest(url, data) {
    const res = await fetch(url, {
        method: "POST",
        body: data ? JSON.stringify(data) : "",
        headers: {
            "Content-Type": "application/json",
        },
    });

    return await res.json();
}

// Updates payment amount of the given reference
async function sendUpdatePaymentAmountRequest(reference, amount) {
    try {
        const res = await sendPostRequest("/admin/update-payment-amount", { reference: reference, amount: amount});
        console.log(res);
        switch (res.status) {
            case "received":
                window.location.href = "admin/result/received/" + reference;
                break;
            default:
                window.location.href = "admin/result/error/" + reference;
                break;
        };
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details");
    }
}

// Binds submit buttons to `update-payment-amount`-endpoint
function bindUpdatePaymentAmountFormButtons() {
    var elements = document.getElementsByName('updatePaymentAmountForm');
    for (var i = 0; i < elements.length;  i++) {
        elements[i].addEventListener('submit', async function(event) {
            event.preventDefault();

            var formData = new FormData(event.target);
            var amount = formData.get('amount') * 100; //  Multiple by 100, so that `12.34` EUR becomes `1234` in minor units
            var reference = formData.get('reference');

            await sendUpdatePaymentAmountRequest(reference, amount);
        });
    }
}

// Binds submit buttons to `update-payment-amount`-endpoint
// The amount cannot be specified here and follows the same logic as `bindUpdatePaymentAmountFormButtons()`
function bindExtendPaymentFormButtons() {
    var elements = document.getElementsByName('extendPaymentForm');
    for (var i = 0; i < elements.length;  i++) {
        elements[i].addEventListener('submit', async function(event) {
            event.preventDefault();

            var formData = new FormData(event.target);
            var amount = formData.get('amount') * 100; //  Multiple by 100, so that `12.34` EUR becomes `1234` in minor units
            var reference = formData.get('reference');

            await sendUpdatePaymentAmountRequest(reference, amount);
        });
    }
}

bindUpdatePaymentAmountFormButtons();
bindExtendPaymentFormButtons();