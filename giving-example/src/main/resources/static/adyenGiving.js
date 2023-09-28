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

async function handleDonation(amount) {
  try {
    console.log(amount);
    const res = await callServer(`/api/donations`, amount);

    switch (res.status) {
      case "completed":
        window.location.href = "/result/donated";
        break;
      default:
        window.location.href = "/result/error";
        break;
    }
  } catch (error) {
    console.error(error);
    alert("Error occurred. Look at console for details");
  }
}

async function startGiving() {
  const checkout= await AdyenCheckout({
    clientKey,
    environment: "test",
  });

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
      link: "https://www.adyen.com/legal/terms-and-conditions" // Replace with yours
    },
    onDonate: (state, component) => {
      if(state.isValid) {
        console.log("Initiating donation");
        handleDonation(state.data.amount);
      }
    },
    onCancel: (result, component) => {
      console.log("Donation cancelled");
      console.log(result);
      document.getElementById('donation-container').style.display = 'none';
    }
  };

  try {
    checkout.create('donation', donationConfig).mount('#donation-container');
  } catch (ex) {
    console.warn(ex);
  }
}

startGiving();