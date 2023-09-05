# Adyen [online payment](https://docs.adyen.com/online-payments) integration demo - Sessions Flow

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spring-online-payments/tree/main/checkout-example)  
[First time with Gitpod?](https://github.com/adyen-examples/.github/blob/main/pages/gitpod-get-started.md)

## Details

This repository showcases a PCI-compliant integration of the **Session Flow**, the default integration that we recommend for merchants. Explore this simplified e-commerce demo to discover the code, libraries and configuration you need to enable various payment options in your checkout experience.  

It includes a **Java + Spring Boot + Thymeleaf** application that supports [Adyen Drop-in and Components](https://docs.adyen.com/online-payments/build-your-integration) 
(ACH, Alipay, Cards, Dotpay, iDEAL, Klarna, PayPal, etc..) using the Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library)).

> **Note**
For more [advanced use cases](https://docs.adyen.com/online-payments/build-your-integration/additional-use-cases/) check out the **Advanced Flow** demo in the `../checkout-example-advanced` folder.
>

![Card checkout demo](src/main/resources/static/images/cardcheckout.gif)


## Requirements

-   Java 17
-   Network access to maven central

## Installation

1. Clone this repo:

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```

## Usage

### Set the environment variables
Set environment variables for the required configuration
    - [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key)
    - [Client Key](https://docs.adyen.com/user-management/client-side-authentication)
    - [Merchant Account](https://docs.adyen.com/account/account-structure)
    - [HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures)


On Linux/Mac export env variables
```shell
export ADYEN_API_KEY=yourAdyenApiKey
export ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
export ADYEN_CLIENT_KEY=yourAdyenClientKey
export ADYEN_HMAC_KEY=yourHmacKey
```

On Windows CMD you can use below commands instead
```shell
set ADYEN_API_KEY=yourAdyenApiKey
set ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
set ADYEN_CLIENT_KEY=yourAdyenClientKey
set ADYEN_HMAC_KEY=yourHmacKey
```

Alternatively it is possible to define the settings in the `application.properties`
```# application.properties
ADYEN_API_KEY=yourAdyenApiKey
ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
ADYEN_CLIENT_KEY=yourAdyenClientKey
ADYEN_HMAC_KEY=yourHmacKey
```

### Configure allowed origins (CORS)

It is necessary to specify the domains or URLs that will make requests to Adyen.

In the Customer Area add `http://localhost:8080` in the list of Allowed Origins associated to the Client Key (API credential).

### Run the demo

Start the server:

```
 cd checkout-example
    
./gradlew bootRun
```

Visit [http://localhost:8080/](http://localhost:8080/) to choose an integration type.

To try out the different payment methods with our [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers) and other payment method details.

## Webhooks

Webhooks deliver asynchronous notifications obout payment status (like authorisation) and other events that are important
to receive and process. You can find more information about webhooks in [this blog post](https://www.adyen.com/knowledge-hub/consuming-webhooks).

This sample application requires the following webhook(s):
* **Standard webhook** to receive the final payment authorisation

Read below how to setup, consume and test the webhook(s).

### Setup a webhook

In the Customer Area in the "Developers" section [create](https://docs.adyen.com/development-resources/webhooks/#set-up-webhooks-in-your-customer-area) a new Standard webhook

Copy the generated HMAC Key and set it as the environment variable (as explained above).

Make sure the webhook is **enabled** (therefore it can receive the notifications).

### Expose an endpoint

The demo provides a simple webhook implementation exposed at `/api/webhooks/notifications` that will show you how to
receive, validate and consume the webhook payload.

### Test your webhook

Testing webhooks is not trivial: the application runs on your `localhost` or on a different server/cloud, and the Adyen
platform must be able to reach it. 

Check out our [Webhooks Testing guide](https://github.com/adyen-examples/.github/blob/main/pages/webhooks-testing.md).

