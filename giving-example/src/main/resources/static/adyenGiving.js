const clientKey = document.getElementById("clientKey").innerHTML;

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

async function startGiving() {

  const paymentMethodsResponse = await callServer("/api/getPaymentMethods");
  const checkout= await AdyenCheckout(
    {
      clientKey,
      locale: "en_US",
      environment: "test",
      showPayButton: true,
      paymentMethodsResponse: paymentMethodsResponse,
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
      },
    }
  );

  const donationConfig = {
    amounts: {
      currency: "EUR",
      values: [300, 500, 1000]
    },
    // backgroundUrl: "https://www.adyen.com/dam/jcr:38701562-9572-4aae-acd3-ed53e220d63b/social-responsibility-city-illustration.svg",
    description: "The Charitable Foundation is a non-profit aiming at showing you the power of Adyen Giving",
    logoUrl: "https://www.adyen.com/dam/jcr:49277359-f3b5-4ceb-b54c-08189ae2433e/hands-rockon-icon-green.svg",
    name: "The Charitable Foundation",
    url: "https://www.adyen.com/social-responsibility/giving",
    showCancelButton: true,
    disclaimerMessage: {
      message: "By donating you agree to the %#terms%#",
      linkText: "terms and conditions",
      link: "https://www.adyen.com/terms-and-conditions"


    },
    onDonate: (state, component) => {
      if(state.isValid) {
        console.log("Initiating donation");
        let donationToken = sessionStorage.getItem("donationToken");
        let pspReference = sessionStorage.getItem("pspReference");

        if(!donationToken || !pspReference) {
          console.log("No token or pspReference found, can't donate");
        }
        else{
          // TODO : Send the full state.data once I found the proper type for it
          callServer(`/api/donations?donationToken=${donationToken}&pspReference=${pspReference}`, state.data.amount);
        }

      }

      // state.isValid // True or false. Specifies if the shopper has selected a donation amount.
      // state.data // Provides the data that you need to pass in the `/donations` call.
      // component // Provides the active Component instance that called this event.
    },
    onCancel: (result, component) => {console.log("Donation cancelled");}
  };

  const donation = checkout.create('donation', donationConfig).mount('#donation-container');
  console.log("Adyen Giving loaded");
}

startGiving();
