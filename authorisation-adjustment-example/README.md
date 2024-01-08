# Adyen [Authorisation Adjustment](https://docs.adyen.com/online-payments/classic-integrations/modify-payments/adjust-authorisation) Integration Demo

## Run demo in one-click
[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spring-online-payments/tree/main/authorisation-adjustment-example)

[First time with Gitpod?](https://github.com/adyen-examples/.github/blob/main/pages/gitpod-get-started.md)


## Description

This repository includes an adjust authorisation example for the following three use cases after a pre-authorised payment: incremental, decremental adjustments. Within this demo app, you'll find a simplified version of a hotel booking, where the shopper perform a booking and administrators can **[1] adjust** (increase/decrease) the payment amount, **[2] extend** the authorisation expiry date, **[3] capture** the final amount and **[4] reverse** (cancel or refund) an authorised payment.

> **Note:** We've included a technical [blog post](https://www.adyen.com/knowledge-hub/pre-authorizations-and-authorization-adjustments-for-developers) that explains every step of this demo.

![Authorisation Adjustment Card Demo](src/main/resources/static/images/cardauthorisationadjustment.gif)


This demo leverages Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library) | [Docs](https://docs.adyen.com/development-resources/libraries#java)).

## Requirements
- [Adyen API Credentials](https://docs.adyen.com/development-resources/api-credentials/)
- Java 17

## 1. Installation

Clone this repository:

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```


## 2. Set the environment variables
Create a `./.env` file with all required configuration
   - [Adyen API key](https://docs.adyen.com/user-management/how-to-get-the-api-key)
   - [Adyen Client Key](https://docs.adyen.com/user-management/client-side-authentication)
   - [Adyen Merchant Account](https://docs.adyen.com/account/account-structure)
   - [Adyen HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures)

In your Customer Area, remember to include `http://localhost:8080` in the list of `Allowed Origins` to allow the Adyen.Component to load.

```
PORT=8080
ADYEN_API_KEY="your_API_key_here"
ADYEN_MERCHANT_ACCOUNT="your_merchant_account_here"
ADYEN_CLIENT_KEY="your_client_key_here"
ADYEN_HMAC_KEY="your_hmac_key_here"
```

3. Run the application

```
./gradlew bootRun
```

4. Usage

To try out this application with test card numbers, visit [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers). We recommend saving multiple test cards in your browser so you can test your integration faster in the future.

1. Make a booking in the `Booking View`
2. Visit the `Admin Panel` to see the incoming webhooks and perform operations on the initial preauthorisation.

A success scenario for a payment followed by two adjustments, a capture and a reversal looks like:

`AUTHORISATION` (preauthorisation) → `AUTHORISATION_ADJUSTMENT` (adjust) → `AUTHORISATION_ADJUSTMENT` (adjust) → `CAPTURE` (capture) → `CANCEL_OR_REFUND` (reversal)

Adyen expires an authorisation request automatically after XX days depending on the card brand.
The `EXTEND` operation in this sample is used to extend the expiry date manually, for the exact days, refer to the [documentation](https://docs.adyen.com/online-payments/adjust-authorisation/#validity) (section: validity).

When CAPTURE is executed, it will perform the operation on the latest amount. You'll have to wait for the `AUTHORISATION_ADJUSTMENT` response, before making the capture once it's final.

# Webhooks

Webhooks deliver asynchronous notifications about the payment status and other events that are important to receive and process.
You can find more information about webhooks in [this blog post](https://www.adyen.com/knowledge-hub/consuming-webhooks).

### Webhook setup

In the Customer Area under the `Developers → Webhooks` section, [create](https://docs.adyen.com/development-resources/webhooks/#set-up-webhooks-in-your-customer-area) a new `Standard webhook`.

A good practice is to set up basic authentication, copy the generated HMAC Key and set it as an environment variable. The application will use this to verify the [HMAC signatures](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures/).

Make sure the webhook is **enabled**, so it can receive notifications.

### Expose an endpoint

This demo provides a simple webhook implementation exposed at `/api/webhooks/notifications` that shows you how to receive, validate and consume the webhook payload.

### Test your webhook

The following webhooks `events` should be enabled:
* **AUTHORISATION**
* **AUTHORISATION_ADJUSTMENT**
* **CAPTURE**
* **CANCEL_OR_REFUND**
* **REFUND_FAILED**
* **REFUNDED_REVERSED**

To make sure that the Adyen platform can reach your application, we have written a [Webhooks Testing Guide](https://github.com/adyen-examples/.github/blob/main/pages/webhooks-testing.md)
that explores several options on how you can easily achieve this (e.g. running on localhost or cloud).


## Contributing

We commit all our new features directly into our GitHub repository. Feel free to request or suggest new features or code changes yourself as well!

Find out more in our [Contributing](https://github.com/adyen-examples/.github/blob/main/CONTRIBUTING.md) guidelines.

## License

MIT license. For more information, see the **LICENSE** file in the root directory.

> **Note** We currently store these values in a local memory cache, if you restart/stop the application these values are lost. However, the tokens will still be persisted on the Adyen Platform.
> You can view the stored payment details by going to a recent payment of the shopper in the Customer Area: `Transactions` → `Payments` → `Shopper Details` → `Recurring: View stored payment details`.


