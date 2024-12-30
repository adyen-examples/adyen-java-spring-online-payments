const clientKey = document.getElementById("clientKey").innerHTML;
const { AdyenCheckout, Card } = window.AdyenWeb;

async function startCheckout() {
    try {
        const paymentMethodsResponse = await fetch("/api/paymentMethods", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            }
        }).then(response => response.json());

        const configuration = {
            paymentMethodsResponse: paymentMethodsResponse,
            clientKey,
            locale: "en_US",
            countryCode: 'NL',
            environment: "test",
            showPayButton: true,
            translations: {
                'en-US': {
                    'creditCard.securityCode.label': 'CVV/CVC'
                }
            },
            onSubmit: async (state, component, actions) => {
                console.info("onSubmit", state, component, actions);
                try {
                    if (state.isValid) {
                        const {action, order, resultCode, donationToken} = await fetch("/api/payments", {
                            method: "POST",
                            body: state.data ? JSON.stringify(state.data) : "",
                            headers: {
                                "Content-Type": "application/json",
                            }
                        }).then(response => response.json());

                        if (!resultCode) {
                            actions.reject();
                        }

                        actions.resolve({
                            resultCode,
                            action,
                            order,
                            donationToken
                        });
                    }
                } catch (error) {
                    console.error(error);
                    actions.reject();
                }
            },
            onPaymentCompleted: (result, component) => {
                console.info("onPaymentCompleted", result, component);
                handleOnPaymentCompleted(result, component);
            },
            onPaymentFailed: (result, component) => {
                console.info("onPaymentFailed", result, component);
                handleOnPaymentFailed(result, component);
            },
            onError: (error, component) => {
                console.error("onError", error.name, error.message, error.stack, component);
                window.location.href = "/result/error";
            },
            onAdditionalDetails: async (state, component) => {
                console.info("onAdditionalDetails", state, component);
                const response = await fetch("/api/payments/details", {
                    method: "POST",
                    body: state.data ? JSON.stringify(state.data) : "",
                    headers: {
                        "Content-Type": "application/json",
                    }
                }).then(response => response.json());

                if (response.action) {
                    component.handleAction(response.action);
                } else {
                    handleOnPaymentCompleted(response);
                }
            }
        };

        const paymentMethodsConfiguration = {
            card: {
                showBrandIcon: true,
                hasHolderName: true,
                holderNameRequired: true,
                name: "Credit or debit card",
                amount: {
                    value: 10000,
                    currency: "EUR",
                },
                placeholders: {
                    cardNumber: '1234 5678 9012 3456',
                    expiryDate: 'MM/YY',
                    securityCodeThreeDigits: '123',
                    securityCodeFourDigits: '1234',
                    holderName: 'J. Smith'
                }
            }
        };

        // Start the AdyenCheckout and mount the element onto the 'payment' div.
        const adyenCheckout = await AdyenCheckout(configuration);
        const card = new Card(adyenCheckout, {
            // Optional configuration.
            billingAddressRequired: false, // when true show the billing address input fields and mark them as required.
            showBrandIcon: true, // when false not showing the brand logo
            hasHolderName: true, // show holder name
            holderNameRequired: true, // make holder name mandatory
            // configure placeholders
            placeholders: {
                cardNumber: '1234 5678 9012 3456',
                expiryDate: 'MM/YY',
                securityCodeThreeDigits: '123',
                securityCodeFourDigits: '1234',
                holderName: 'J. Smith'
            }
        }).mount(document.getElementById("payment"));
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details.");
    }
}

// Function to handle payment completion redirects
function handleOnPaymentCompleted(response) {
    switch (response.resultCode) {
        case "Authorised":
            window.location.href = "/result/success";
            break;
        case "Pending":
        case "Received":
            window.location.href = "/result/pending";
            break;
        default:
            window.location.href = "/result/error";
            break;
    }
}

// Function to handle payment failure redirects
function handleOnPaymentFailed(response) {
    switch (response.resultCode) {
        case "Cancelled":
        case "Refused":
            window.location.href = "/result/failed";
            break;
        default:
            window.location.href = "/result/error";
            break;
    }
}

startCheckout();
