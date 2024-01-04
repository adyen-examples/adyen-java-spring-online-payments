# Adyen [Authorisation Adjustment](https://docs.adyen.com/online-payments/classic-integrations/modify-payments/adjust-authorisation) Integration Demo

This repository includes an adjust authorisation example for the following three use cases after a pre-authorised payment: incremental, decremental adjustments. Within this demo app, you'll find a simplified version of a hotel booking, where the shopper perform a booking and administrators can **[1] adjust** (increase/decrease) the payment amount, **[2] extend** the authorisation expiry date, **[3] capture** the final amount and **[4] reverse** (cancel or refund) an authorised payment.

> **Note:** We've included a technical [blog post](https://www.adyen.com/knowledge-hub/pre-authorizations-and-authorization-adjustments-for-developers) that explains every step of this demo.

![Authorisation Adjustment Card Demo](src/main/resources/static/images/cardauthorisationadjustment.gif)

## Run this integration in seconds using [Gitpod](https://gitpod.io/)

* Open your [Adyen Test Account](https://ca-test.adyen.com/ca/ca/overview/default.shtml) and create a set of [API keys](https://docs.adyen.com/user-management/how-to-get-the-api-key).
* Go to [gitpod account variables](https://gitpod.io/variables).
* Set the `ADYEN_API_KEY`, `ADYEN_CLIENT_KEY`, `ADYEN_HMAC_KEY` and `ADYEN_MERCHANT_ACCOUNT` variables.
* Click the button below!

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spring-online-payments/tree/main/authorisation-adjustment-example)

_NOTE: To allow the Adyen Drop-In and Components to load, you have to add `https://*.gitpod.io` as allowed origin for your chosen set of [API Credentials](https://ca-test.adyen.com/ca/ca/config/api_credentials_new.shtml)_


This demo leverages Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library) | [Docs](https://docs.adyen.com/development-resources/libraries#java)).

## Requirements
- [Adyen API Credentials](https://docs.adyen.com/development-resources/api-credentials/)
- Java 17

## Installation

Clone this repository:

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```


## Usage

1. Create a `./.env` file with all required configuration
   - [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key)
   - [Client Key](https://docs.adyen.com/user-management/client-side-authentication)
   - [Merchant Account](https://docs.adyen.com/account/account-structure)
   - [HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures)

In your Customer Area, remember to include `http://localhost:8080` in the list of `Allowed Origins`.

```
PORT=8080
ADYEN_API_KEY="your_API_key_here"
ADYEN_MERCHANT_ACCOUNT="your_merchant_account_here"
ADYEN_CLIENT_KEY="your_client_key_here"
ADYEN_HMAC_KEY="your_hmac_key_here"
```

2. Start the application:

```
./gradlew bootRun
```

3. Visit [http://localhost:8080/](http://localhost:8080/).

To try out this application with test card numbers, visit [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers). We recommend saving multiple test cards in your browser so you can test your integration faster in the future.

1. Make a booking in the `Booking View`
2. Visit the `Admin Panel` to see the incoming webhooks and perform operations on the initial preauthorisation.


A success scenario for a payment followed by two adjustments, a capture and a reversal looks like:

`AUTHORISATION` (preauthorisation) → `AUTHORISATION_ADJUSTMENT` (adjust) → `AUTHORISATION_ADJUSTMENT` (adjust) → `CAPTURE` (capture) → `CANCEL_OR_REFUND` (reversal)

Adyen expires an authorisation request automatically after XX days depending on the card brand.
The `EXTEND` operation in this sample is used to extend the expiry date manually, for the exact days, refer to the [documentation](https://docs.adyen.com/online-payments/adjust-authorisation/#validity) (section: validity).

When CAPTURE is executed, it will perform the operation on the latest amount. You'll have to wait for the `AUTHORISATION_ADJUSTMENT` response, before making the capture once it's final.


## Testing webhooks

Webhooks deliver asynchronous notifications and it is important to test them during the setup of your integration. You can find more information about webhooks in [this detailed blog post](https://www.adyen.com/blog/Integrating-webhooks-notifications-with-Adyen-Checkout).

This sample application provides a simple webhook integration exposed at `/api/webhooks/notifications`. For it to work, you need to:

1. Provide a way for the Adyen platform to reach your running application
2. Add a Standard webhook in your Customer Area

### Making your server reachable

Your endpoint that will consume the incoming webhook must be publicly accessible.

There are typically 3 options:
* deploy on your own cloud provider
* deploy on Gitpod
* expose your localhost with tunneling software (i.e. ngrok)

#### Option 1: cloud deployment
If you deploy on your cloud provider (or your own public server) the webhook URL will be the URL of the server
```
  https://{cloud-provider}/api/webhooks/notifications
```

#### Option 2: Gitpod
If you use Gitpod the webhook URL will be the host assigned by Gitpod
```
  https://myorg-myrepo-y8ad7pso0w5.ws-eu75.gitpod.io/api/webhooks/notifications
```
**Note:** when starting a new Gitpod workspace the host changes, make sure to **update the Webhook URL** in the Customer Area

#### Option 3: localhost via tunneling software
If you use a tunneling service like [ngrok](ngrok) the webhook URL will be the generated URL (ie `https://c991-80-113-16-28.ngrok.io`)

```bash
  $ ngrok http 8080

  Session Status                online
  Account                       ############
  Version                       #########
  Region                        United States (us)
  Forwarding                    http://c991-80-113-16-28.ngrok.io -> http://localhost:8080
  Forwarding                    https://c991-80-113-16-28.ngrok.io -> http://localhost:8080
```

**Note:** when restarting ngrok a new URL is generated, make sure to **update the Webhook URL** in the Customer Area

### Set up a webhook

* In the Customer Area go to Developers -> Webhooks and create a new 'Standard notification' webhook.
* Enter the URL of your application/endpoint (see options [above](#making-your-server-reachable))
* Define username and password for Basic Authentication
* Generate the HMAC Key
* Optionally, in Additional Settings, add the data you want to receive. A good example is 'Payment Account Reference'.
* Make sure the webhook is **Enabled** (therefore it can receive the notifications)

That's it! Every time you perform a new payment, your application will receive a notification from the Adyen platform.

## Contributing

We commit all our new features directly into our GitHub repository. Feel free to request or suggest new features or code changes yourself as well!

Find out more in our [Contributing](https://github.com/adyen-examples/.github/blob/main/CONTRIBUTING.md) guidelines.

## License

MIT license. For more information, see the **LICENSE** file in the root directory.

















_____
# Adyen [Tokenization](https://docs.adyen.com/online-payments-tokenization) Integration Demo

This repository includes a tokenization example for subscriptions. Within this demo app, you'll find a simplified version of a website that offers a music subscription service.
The shopper can purchase a subscription and administrators can manage the saved (tokenized) payment methods on a separate admin panel.
The panel allows admins to make payments on behalf of the shopper using this token. We refer to this token as `recurringDetailReference` in this application.

## Workflow

The sample app implements the following workflow:

* send a zero-auth transaction to request the Recurring Payment
* receive the webhook with the token (`recurringDetailReference`)
* perform a payment using the token
* receive the webhook with the payment authorisation

> **Note:** Checkout the technical [blog post](https://www.adyen.com/blog/use-adyen-tokenization-to-implement-recurring-in-dotnet) that explains every step of this demo.

![Subscription Demo](public/images/cardsubscription.gif)


## Run integration on [Gitpod](https://gitpod.io/)
1. Open your [Adyen Test Account](https://ca-test.adyen.com/ca/ca/overview/default.shtml) and create a set of [API keys](https://docs.adyen.com/user-management/how-to-get-the-api-key).
    - [`ADYEN_API_KEY`](https://docs.adyen.com/user-management/how-to-get-the-api-key)
    - [`ADYEN_CLIENT_KEY`](https://docs.adyen.com/user-management/client-side-authentication)
    - [`ADYEN_MERCHANT_ACCOUNT`](https://docs.adyen.com/account/account-structure)


2. Go to [Gitpod Environmental Variables](https://gitpod.io/variables) and set the following variables: [`ADYEN_API_KEY`](https://docs.adyen.com/user-management/how-to-get-the-api-key), [`ADYEN_CLIENT_KEY`](https://docs.adyen.com/user-management/client-side-authentication) and [`ADYEN_MERCHANT_ACCOUNT`](https://docs.adyen.com/account/account-structure) with a scope of `*/*`


3. To allow the Adyen Drop-In and Components to load, add `https://*.gitpod.io` as allowed origin by going to your `ADYEN_MERCHANT_ACCOUNT` in the Customer Area: `Developers` → `API credentials` → Find your `ws_user` → `Client settings` → `Add Allowed origins`.
> **Warning** You should only allow wild card (*) domains in the **test** environment. In a **live** environment, you should specify the exact URL of the application.

This demo provides a simple webhook integration at `/api/webhooks/notifications`. For it to work, you need to provide a way for Adyen's servers to reach your running application on Gitpod and add a standard webhook in the Customer Area.


4. To receive notifications asynchronously, add a webhook:
    - In the Customer Area go to `Developers` → `Webhooks` and add a new `Standard notification webhook`
    - Define username and password (Basic Authentication) to [protect your endpoint](https://docs.adyen.com/development-resources/webhooks/best-practices#security) - Basic authentication only guarantees that the notification was sent by Adyen, not that it wasn't modified during transmission
    - Generate the [HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures) and set the `ADYEN_HMAC_KEY` in your [Gitpod Environment Variables](https://gitpod.io/variables) with a scope of `*/*` -  This key is used to [verify](https://docs.adyen.com/development-resources/webhooks/best-practices#security) whether the HMAC signature that is included in the notification, was sent by Adyen and not modified during transmission
    - For the URL, enter `https://gitpod.io` for now, we will need to update this webhook URL in step 7
    - Make sure that the `Recurring contract` setting is **enabled** on `Merchant` account-level - In the `Customer Area`, under `Developers` -> `Webhooks` -> `Settings` -> Enable `Recurring contract` on `Merchant`-level and hit "Save".
    - Make sure that your webhook sends the `RECURRING_CONTRACT` event when you've created the webhook
    - Make sure the webhook is **enabled** to send notifications


5. In the Customer Area, go to `Developers` → `Additional Settings` → Under `Payment` enable `Recurring Details` for subscriptions.


6. Click the button below to launch the application in Gitpod.

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spring-online-payments/tree/main/authorisation-adjustment-example)

7. Update your webhook in the Customer Area with the public url that is generated by Gitpod
    - In the Customer Area, go to `Developers` → `Webhooks` → Select your `Webhook` that is created in step 4 → `Server Configuration`
    - Update the URL of your application/endpoint (e.g. `https://8080-myorg-myrepo-y8ad7pso0w5.ws-eu75.gitpod.io/api/webhooks/notifications/`
    - Hit `Apply` → `Save changes` and Gitpod should be able to receive notifications

> **Note** When exiting Gitpod a new URL is generated, make sure to **update the Webhook URL** in the Customer Area as described in the final step.
> You can find more information about webhooks in [this detailed blog post](https://www.adyen.com/blog/Integrating-webhooks-notifications-with-Adyen-Checkout).


## Run integration on localhost using a proxy
You will need Java 17 to run this application locally.

1. Clone this repository.

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```

2. Open your [Adyen Test Account](https://ca-test.adyen.com/ca/ca/overview/default.shtml) and create a set of [API keys](https://docs.adyen.com/user-management/how-to-get-the-api-key).
    - [`ADYEN_API_KEY`](https://docs.adyen.com/user-management/how-to-get-the-api-key)
    - [`ADYEN_CLIENT_KEY`](https://docs.adyen.com/user-management/client-side-authentication)
    - [`ADYEN_MERCHANT_ACCOUNT`](https://docs.adyen.com/account/account-structure)


3. To allow the Adyen Drop-In and Components to load, add `https://localhost:8080` as allowed origin by going to your MerchantAccount in the Customer Area: `Developers` → `API credentials` → Find your `ws_user` → `Client settings` → `Add Allowed origins`.
> **Warning** You should only allow wild card (*) domains in the **test** environment. In a **live** environment, you should specify the exact URL of the application.

This demo provides a simple webhook integration at `/api/webhooks/notifications`. For it to work, you need to provide a way for Adyen's servers to reach your running application and add a standard webhook in the Customer Area.
To expose this endpoint locally you can use a tunneling software (see point 4)

4. Expose your localhost with tunneling software (i.e. ngrok).
    - Add `https://*.ngrok.io` to your allowed origins

If you use a tunneling service like ngrok, the webhook URL will be the generated URL (i.e. `https://c991-80-113-16-28.ngrok.io/api/webhooks/notifications/`).

```bash
  $ ngrok http 8080

  Session Status                online
  Account                       ############
  Version                       #########
  Region                        United States (us)
  Forwarding                    http://c991-80-113-16-28.ngrok.io -> http://localhost:8080
  Forwarding                    https://c991-80-113-16-28.ngrok.io -> http://localhost:8080
```

6. To receive notifications asynchronously, add a webhook:
    - In the Customer Area go to `Developers` → `Webhooks` and add a new `Standard notification webhook`
    - Define username and password (Basic Authentication) to [protect your endpoint](https://docs.adyen.com/development-resources/webhooks/best-practices#security) - Basic authentication only guarantees that the notification was sent by Adyen, not that it wasn't modified during transmission
    - Generate the [HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures) - This key is used to [verify](https://docs.adyen.com/development-resources/webhooks/best-practices#security) whether the HMAC signature that is included in the notification, was sent by Adyen and not modified during transmission
    - See script below that allows you to easily set your environmental variables
    - For the URL, enter `https://ngrok.io` for now - We will need to update this webhook URL in step 10
    - Make sure that the `Recurring contract` setting is **enabled** on `Merchant` account-level - In the `Customer Area`, under `Developers` -> `Webhooks` -> `Settings` -> Enable `Recurring contract` on `Merchant`-level and hit "Save".
    - Make sure that your webhook sends the `RECURRING_CONTRACT` event when you've created the webhook
    - Make sure the webhook is **enabled** to send notifications


7. Set the following environment variables in your terminal environment: `ADYEN_API_KEY`, `ADYEN_CLIENT_KEY`, `ADYEN_MERCHANT_ACCOUNT` and `ADYEN_HMAC_KEY`. Note that some IDEs will have to be restarted for environmental variables to be injected properly.

```shell
export ADYEN_API_KEY=yourAdyenApiKey
export ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
export ADYEN_CLIENT_KEY=yourAdyenClientKey
export ADYEN_HMAC_KEY=yourAdyenHmacKey
```

On Windows CMD you can use this command instead.

```shell
set ADYEN_API_KEY=yourAdyenApiKey
set ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
set ADYEN_CLIENT_KEY=yourAdyenClientKey
set ADYEN_HMAC_KEY=yourAdyenHmacKey
```

Alternatively it is possible to define the settings in the `application.properties`
```# application.properties
ADYEN_API_KEY=yourAdyenApiKey
ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
ADYEN_CLIENT_KEY=yourAdyenClientKey
ADYEN_HMAC_KEY=yourHmacKey
```
8. In the Customer Area, go to `Developers` → `Additional Settings` → Under `Payment` enable `Recurring Details` for subscriptions.


9. Start the application and visit localhost.

```
./gradlew bootRun
```

10. Update your webhook in your Customer Area with the public url that is generated.
    - In the Customer Area go to `Developers` → `Webhooks` → Select your `Webhook` that is created in step 6 → `Server Configuration`
    - Update the URL of your application/endpoint (e.g. `https://c991-80-113-16-28.ngrok.io/api/webhooks/notifications/`)
    - Hit `Apply` → `Save changes` and Gitpod should be able to receive notifications

> **Note** When exiting ngrok or Visual Studio a new URL is generated, make sure to **update the Webhook URL** in the Customer Area as described in the final step.
> You can find more information about webhooks in [this detailed blog post](https://www.adyen.com/blog/Integrating-webhooks-notifications-with-Adyen-Checkout).



## Usage
To try out this application with test card numbers, visit [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers). We recommend saving multiple test cards in your browser so you can test your integration faster in the future.

1. Visit the main page 'Shopper View' to test the application, enter one or multiple card details. Once the payment is authorized, you will receive a webhook notification with the recurringDetailReference. Enter multiple cards to receive multiple different recurringDetailReferences.

2. Visit 'Admin Panel' to find the saved recurringDetailReferences and choose to make a payment request or disable the recurringDetailReference.

3. Visit the Customer Area `Developers` → `API logs` to view your logs.

> **Note** We currently store these values in a local memory cache, if you restart/stop the application these values are lost. However, the tokens will still be persisted on the Adyen Platform.
> You can view the stored payment details by going to a recent payment of the shopper in the Customer Area: `Transactions` → `Payments` → `Shopper Details` → `Recurring: View stored payment details`.


