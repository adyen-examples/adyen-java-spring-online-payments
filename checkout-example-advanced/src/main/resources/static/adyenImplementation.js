const clientKey = document.getElementById("clientKey").innerHTML;

// Used to finalize a checkout call in case of redirect
const urlParams = new URLSearchParams(window.location.search);
const sessionId = urlParams.get('sessionId'); // Unique identifier for the payment session
const redirectResult = urlParams.get('redirectResult');

async function initCheckout() {

  try {
    const paymentMethodsResponse = await callServer("/api/getPaymentMethods");
    const checkout = await createAdyenCheckout(paymentMethodsResponse);
    checkout.create(type).mount(document.getElementById("payment"));

  } catch (error) {
    console.error(error);
    alert("Error occurred. Look at console for details");
  }
}

async function createAdyenCheckout(paymentMethodsResponse){
  return new AdyenCheckout(
    {
      paymentMethodsResponse: filterUnimplemented(paymentMethodsResponse),
      clientKey,
      locale: "en_US",
      environment: "test",
      showPayButton: true,
      paymentMethodsConfiguration: {
        ideal: {
          showImage: true,
        },
        card: {
          hasHolderName: true,
          holderNameRequired: true,
          name: "Credit or debit card",
          amount: {
            value: 10000,  // in minor units
            currency: "EUR",
          },
        },
        paypal: {
          amount: {
            value: 10000, // in minor units
            currency: "USD",
          },
          environment: "test", // Change this to "live" when you're ready to accept live PayPal payments
          countryCode: "NL", // Only needed for test. This will be automatically retrieved when you are in production.
        }
      },
      onSubmit: (result, component) => {
        if (result.isValid) {
          handleSubmission(result, component, "/api/initiatePayment");
        }
      },
      onAdditionalDetails: (result, component) => {
        handleSubmission(result, component, "/api/submitAdditionalDetails");
      },
      onError: (error, component) => {
        console.error("onError");
        console.error(error.name, error.message, error.stack, component);
        handleServerResponse(error, component);
      },
    }
  );
}

// Event handlers called when the shopper selects the pay button,
// or when additional information is required to complete the payment
async function handleSubmission(state, component, url) {
  try {
    const res = await callServer(url, state.data);
    handleServerResponse(res, component);
  } catch (error) {
    console.error(error);
    alert("Error occurred. Look at console for details");
  }
}

// Calls your server endpoints
async function callServer(url, data) {
  const res = await fetch(url, {
    method: "POST",
    body: data ? JSON.stringify(data) : "",
    headers: {
      "Content-Type": "application/json",
    },
  });

  return await res.json();
}

// Handles responses sent from your server to the client
function handleServerResponse(res, component) {
  if (res.action) {
    component.handleAction(res.action);
  } else {
    switch (res.resultCode) {
      case "Authorised":
        window.location.href = "/result/success";
        break;
      case "Pending":
      case "Received":
        window.location.href = "/result/pending";
        break;
      case "Refused":
        window.location.href = "/result/failed";
        break;
      default:
        window.location.href = "/result/error";
        break;
    }
  }
}

function filterUnimplemented(pm) {
  pm.paymentMethods = pm.paymentMethods.filter((it) =>
    [
      "scheme",
      "ideal",
      "dotpay",
      "giropay",
      "directEbanking",
      "ach",
      "alipay",
      "klarna_paynow",
      "klarna",
      "klarna_account",
      "paypal",
    ].includes(it.type)
  );
  return pm;
}

initCheckout();
