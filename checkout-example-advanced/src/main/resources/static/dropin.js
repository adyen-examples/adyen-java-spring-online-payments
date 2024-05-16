const { AdyenCheckout, Dropin } = window.AdyenWeb;

const clientKey = document.getElementById("clientKey").innerHTML;

async function initCheckout() {
  try {
    const paymentMethodsResponse = await callServer("/api/getPaymentMethods");

    const checkout = await createAdyenCheckout(paymentMethodsResponse);

    const dropin = new Dropin(checkout, {
      paymentMethodsConfiguration: {},
      instantPaymentTypes: ['applepay', 'googlepay'],
    }).mount(document.getElementById("payment"));

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

async function createAdyenCheckout(paymentMethodsResponse) {

  return AdyenCheckout(
    {
      amount: {
        currency: "EUR",
        value: 10000
      },
      clientKey: clientKey,
      paymentMethodsResponse: paymentMethodsResponse,
      countryCode: "NL",
      locale: "en_US",
      environment: "test",
      onSubmit: async (state, component, actions) => {
        console.info("onSubmit");

        try {

          const { action, order, resultCode, donationToken } = await callServer("/api/initiatePayment", state.data);
          console.log(action, order, resultCode);

          if (!resultCode) actions.reject();

          actions.resolve({
            resultCode,
            action,
            order,
            donationToken
          });
        } catch (error) {
          console.error('## onSubmit - critical error', error);
          actions.reject();
        }
      },
      onAdditionalDetails: async (state, component, actions) => {
        console.info("onAdditionalDetails");

        try {
          const { action, order, resultCode, donationToken } = await callServer("/api/submitAdditionalDetails", state.data);
          console.log(action, order, resultCode);

          if (!resultCode) actions.reject();

          actions.resolve({
            resultCode,
            action,
            order,
            donationToken
          });
        } catch (error) {
          console.error('## onAdditionalDetails - critical error', error);
          actions.reject();
        }
      },
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
      onBalanceCheck: async (resolve, reject, data) => {
        console.log('onBalanceCheck', data);
        //resolve(await checkBalance(data));
      },
      onOrderRequest: async resolve => {
        console.log('onOrderRequested');
        //resolve(await createOrder({ amount }));
      },
      onOrderUpdated: data => {
        console.log('onOrderUpdated', data);
      },
      onOrderCancel: async (order, actions) => {
        console.log('onOrderCancel');
        // await cancelOrder(order);
        // actions.resolve({ amount });
      },
      onActionHandled: rtnObj => {
        console.log('onActionHandled', rtnObj);
      },
      onPaymentMethodsRequest: async (data, { resolve, reject }) => {
        console.log('onPaymentMethodsRequest', data);
        //resolve(await getPaymentMethods({ amount, shopperLocale: data.locale, order: data.order }));
      },
      onError: (error, component) => {
        console.error("onError");
        console.error(error.name, error.message, error.stack, component);
        handleServerResponse(error, component);
      },
    }
  );
}


initCheckout();
