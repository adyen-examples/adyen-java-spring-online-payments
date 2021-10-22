const clientKey = document.getElementById("clientKey").innerHTML;
const type = document.getElementById("type").innerHTML;

async function initCheckout() {

  const urlParams = new URLSearchParams(window.location.search);
  const sessionId = urlParams.get('sessionId');
  const redirectResult = urlParams.get('redirectResult');

  if(!sessionId){
    try {
      const checkoutSessionResponse = await callServer("/api/sessions?type=" + type);
      const configuration = {
        clientKey,
        locale: "en_US",
        environment: "test",
        session: checkoutSessionResponse,
        showPayButton: true,
        paymentMethodsConfiguration: {
          hasHolderName: true,
          holderNameRequired: true,
          billingAddressRequired: true,
          ideal: {
            showImage: true,
          },
          card: {
            hasHolderName: true,
            holderNameRequired: true,
            name: "Credit or debit card",
            amount: {
              value: 1000,
              currency: "EUR",
            },
          },
          paypal: {
            amount: {
              value: 1000,
              currency: "USD",
            },
            environment: "test", // Change this to "live" when you're ready to accept live PayPal payments
            countryCode: "US", // Only needed for test. This will be automatically retrieved when you are in production.
          }
        },
        onPaymentCompleted: (result, component) => {
          console.info("onPaymentCompleted");
          console.info(result, component);
          handleServerResponse(result, component)
        },
        onError: (error, component) => {
          console.error("onError");
          console.error(error.name, error.message, error.stack, component);
          handleServerResponse(error, component)
        },
      };
      // `spring.jackson.default-property-inclusion=non_null` needs to set in
      // src/main/resources/application.properties to avoid NPE here
      const checkout = await new AdyenCheckout(configuration);
      checkout.create(type).mount(document.getElementById("payment"));
    } catch (error) {
      console.error("mounting error");

      console.error(error);
      alert("Error occurred. Look at console for details");
    }

  }
  else{
    try {
      const configuration = {
        clientKey,
          locale: "en_US",
        environment: "test",
        session: {
          id: sessionId, // Unique identifier for the payment session.
        },
        showPayButton: true,
        paymentMethodsConfiguration: {
        hasHolderName: true,
          holderNameRequired: true,
          billingAddressRequired: true,
          ideal: {
          showImage: true,
        },
        card: {
          hasHolderName: true,
            holderNameRequired: true,
            name: "Credit or debit card",
            amount: {
            value: 1000,
              currency: "EUR",
          },
        },
        paypal: {
          amount: {
            value: 1000,
              currency: "USD",
          },
          environment: "test", // Change this to "live" when you're ready to accept live PayPal payments
            countryCode: "US", // Only needed for test. This will be automatically retrieved when you are in production.
        }
      },
        onPaymentCompleted: (result, component) => {
          console.info(result, component);
          handleServerResponse(result, component)
        },
          onError: (error, component) => {
        console.error(error.name, error.message, error.stack, component);
        handleServerResponse(error, component)
      },
      };

      const checkout = await AdyenCheckout(configuration);

      checkout.submitDetails({details: {redirectResult}});
    } catch (error) {
      console.error(error);
      alert("Error occurred. Look at console for details");
    }
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

function handleServerResponse(res, component) {
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

initCheckout();
