const clientKey = document.getElementById("clientKey").innerHTML;
const { AdyenCheckout, Dropin } = window.AdyenWeb;

async function startCheckout() {
  try {
    // Create a new session
    const session = await fetch("/api/sessions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    }).then((response) => response.json());

    const configuration = {
      session: session,
      clientKey,
      environment: "test",
      locale: "en_US",
      countryCode: "NL",
      showPayButton: true,
      translations: {
        "en-US": {
          "creditCard.securityCode.label": "CVV/CVC",
        },
      },
      onPaymentCompleted: (result, component) => {
        console.info("onPaymentCompleted", result, component);
        handleOnPaymentCompleted(result.resultCode, component);
      },
      onPaymentFailed: (result, component) => {
        console.info("onPaymentFailed", result, component);
        handleOnPaymentFailed(result.resultCode, component);
      },
      onError: (error, component) => {
        console.error("onError", error.name, error.message, error.stack, component);
        window.location.href = "/result/error";
      },
    };

    const paymentMethodsConfiguration = {
      card: {
        showBrandIcon: true,
        hasHolderName: true,
        holderNameRequired: true,
        amount: {
          value: 10000,
          currency: "EUR",
        },
        placeholders: {
          cardNumber: "1234 5678 9012 3456",
          expiryDate: "MM/YY",
          securityCodeThreeDigits: "123",
          securityCodeFourDigits: "1234",
          holderName: "J. Smith",
        },
      },
    };

    // Start the AdyenCheckout and mount the element onto the 'payment' div.
    const adyenCheckout = await AdyenCheckout(configuration);
    const dropin = new Dropin(adyenCheckout, {
      paymentMethodsConfiguration: paymentMethodsConfiguration,
    }).mount("#dropin-container");
  } catch (error) {
    console.error(error);
    alert("Error occurred. Look at console for details.");
  }
}

// Function to handle payment completion redirects
function handleOnPaymentCompleted(resultCode) {
  switch (resultCode) {
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
function handleOnPaymentFailed(resultCode) {
  switch (resultCode) {
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
