const { AdyenCheckout, Ideal } = window.AdyenWeb;

const clientKey = document.getElementById("clientKey").innerHTML;

// Used to finalize a checkout call in case of redirect
const urlParams = new URLSearchParams(window.location.search);
const sessionId = urlParams.get('sessionId'); // Unique identifier for the payment session
const redirectResult = urlParams.get('redirectResult');

// Typical checkout experience
async function startCheckout() {

  try {
    const checkoutSessionResponse = await callServer("/api/sessions");

    const checkout = await createAdyenCheckout(checkoutSessionResponse);

    const ideal = new Ideal(checkout, {
      placeholder: "Which bank?"
    }).mount("#payment");

  } catch (error) {
    console.error(error);
    alert("Error occurred. Look at console for details");
  }
}

// Finalise after redirect
async function finalizeCheckout() {
  try {

    const checkout = await createAdyenCheckout({id: sessionId});

    checkout.submitDetails({details: {redirectResult}});

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

if (!sessionId) { startCheckout() } else { finalizeCheckout(); }
