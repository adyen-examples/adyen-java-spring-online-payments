const { AdyenCheckout, GooglePay } = window.AdyenWeb;

const clientKey = document.getElementById("clientKey").innerHTML;


async function startCheckout() {

  try {
    const checkoutSessionResponse = await callServer("/api/sessions");

    const checkout = await createAdyenCheckout(checkoutSessionResponse);

    const googlepay = new GooglePay(checkout, {
      buttonType: "checkout",
      onAuthorized: ({ authorizedEvent, billingAddress, deliveryAddress }, actions) => {
        console.info("onAuthorized");
        console.info(authorizedEvent, billingAddress, deliveryAddress);

        if(true) {
          // perform necessary data check
          actions.resolve();
        } else {
          actions.reject("Insufficient funds unfortunately.");
        }
      }
    }).mount("#payment");

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

function handleServerResponse(res, _component) {
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

// create AdyenCheckout object
async function createAdyenCheckout(session) {

  return AdyenCheckout(
    {
      clientKey,
      locale: "en_US",
      environment: "test",
      session: session,
      showPayButton: true,
      onPaymentCompleted: (result, component) => {
        console.info("onPaymentCompleted");
        console.info(result, component);
        handleServerResponse(result, component);
      },
      onPaymentFailed: (result, component) => {
        console.info("onPaymentFailed");
        console.info(result, component);
        handleServerResponse(result, component);
      },
      onError: (error, component) => {
        console.error("onError");
        console.error(error.name, error.message, error.stack, component);
        handleServerResponse(error, component);
      },
    }
  );
}

startCheckout();
