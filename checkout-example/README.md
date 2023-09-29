# Adyen [Online Payments](https://docs.adyen.com/online-payments) Integration Demo - Sessions Flow

## Run demo in one-click

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spring-online-payments/tree/main/checkout-example)  
&nbsp;[First time with Gitpod?](https://github.com/adyen-examples/.github/blob/main/pages/gitpod-get-started.md)

# Description

This repository showcases a PCI-compliant integration of the **Sessions Flow**, the default integration that we recommend for merchants. Explore this simplified e-commerce demo to discover the code, libraries and configuration you need to enable various payment options in your checkout experience.  

It includes a **Java + Spring Boot + Thymeleaf** application that supports [Adyen Drop-in and Components](https://docs.adyen.com/online-payments/build-your-integration) 
(ACH, Alipay, Cards, Dotpay, iDEAL, Klarna, PayPal, etc..) using the Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library)).   

      
> **Note:**
For a three-steps [advanced use case](https://docs.adyen.com/online-payments/build-your-integration/additional-use-cases/) check out the **Advanced Flow** demo in the [`checkout-example-advanced`](../checkout-example-advanced) folder.

![Card checkout demo](src/main/resources/static/images/cardcheckout.gif)


## Requirements

- [Adyen API Credentials](https://docs.adyen.com/development-resources/api-credentials/)
- Java 17
- Network access to Maven central

## 1. Installation

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```

## 2. Set the environment variables
* [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key)
* [Client Key](https://docs.adyen.com/user-management/client-side-authentication)
* [Merchant Account](https://docs.adyen.com/account/account-structure)
* [HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures)

On Linux/Mac/Windows export/set the environment variables. 
```shell
export ADYEN_API_KEY=yourAdyenApiKey
export ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
export ADYEN_CLIENT_KEY=yourAdyenClientKey
export ADYEN_HMAC_KEY=yourHmacKey
```

Alternatively, it's possible to define the variables in the `application.properties`.
```txt
ADYEN_API_KEY=yourAdyenApiKey
ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
ADYEN_CLIENT_KEY=yourAdyenClientKey
ADYEN_HMAC_KEY=yourHmacKey
```

## 3. Configure allowed origins (CORS)

It is required to specify the domain or URL of the web applications that will make requests to Adyen.

In the Customer Area add `http://localhost:8080` in the list of Allowed Origins associated with the Client Key.

## 4. Run the application

```
cd checkout-example
    
./gradlew bootRun
```

Visit [http://localhost:8080/](http://localhost:8080/) to choose an integration type.

Try out the different payment methods with our [test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers) and other payment method details.

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


To make sure that the Adyen platform can reach your application, we have written a [Webhooks Testing Guide](https://github.com/adyen-examples/.github/blob/main/pages/webhooks-testing.md)
that explores several options on how you can easily achieve this (e.g. running on localhost or cloud).

