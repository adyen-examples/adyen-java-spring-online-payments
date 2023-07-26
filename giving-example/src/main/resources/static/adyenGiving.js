const clientKey = document.getElementById("clientKey").innerHTML;

async function startGiving() {

//Create the configuration object.
  const donationConfig = {
    clientKey,
    locale: "en_US",
    environment: "test",
    amounts: {
      currency: "EUR",
      values: [300, 500, 1000]
    },
    backgroundUrl: "https://example.org/background.png",
    description: "The Charitable Foundation is...",
    logoUrl: "https://example.org/logo.png",
    name: "The Charitable Foundation",
    url: "https://example.org",
    showCancelButton: true,
    onDonate: (result, component) => {
      state.isValid // True or false. Specifies if the shopper has selected a donation amount.
      state.data // Provides the data that you need to pass in the `/donations` call.
      component // Provides the active Component instance that called this event.
      console.log("Ready to donate");
    },
    onCancel: (result, component) => {console.log("Donation cancelled");},
    disclaimerMessage: {
      message: "By donating you agree to the linkText",
      linkText: "terms and conditions",
      link: "https://www.yourcompany.com/terms-and-conditions"
    }
  };

  const checkout = await AdyenCheckout(donationConfig);
  const donation = checkout.create('donation').mount('#donation-container');
  console.log("Adyen Giving loaded");
}

startGiving();
