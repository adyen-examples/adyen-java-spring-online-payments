# Adyen [Pay By Link](https://docs.adyen.com/unified-commerce/pay-by-link) Integration Demo

[![Java CI with Gradle](https://github.com/adyen-examples/adyen-java-spring-online-payments/actions/workflows/build-paybylink.yml/badge.svg)](https://github.com/adyen-examples/adyen-java-spring-online-payments/actions/workflows/paybylink.yml) 

If you want to know more about Pay by link, check out our related [blog post](https://www.adyen.com/blog/pay-by-link-for-developers) or the [documentation](https://docs.adyen.com/checkout/pay-by-link).

## Run demo in one-click
[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new/adyen-examples/adyen-java-spring-online-payments?ref=main&dev_container_path=.devcontainer%2Fpaybylink-example%2Fdevcontainer.json)

## Details


This repository demonstrates a very minimal application allowing you to create payment links, and monitor their status.
Bear in mind that the list of payment links is only stored in memory and will be lost once the application is stopped.

The Demo leverages Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library) | [Docs](https://docs.adyen.com/development-resources/libraries#java)).

![Pay By Link Demo](src/main/resources/images/paybylink.gif)


## Requirements

-   Java 17
-   Node 17 (to build the frontend. See `build.gradle` for details)
-   Network access to maven central

## Installation

1. Clone this repo:

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```

## Usage

1. Set environment variables for the required configuration
    - [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key)
    - [Merchant Account](https://docs.adyen.com/account/account-structure)
    - [HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures)

Remember to include `http://localhost:8080` in the list of Allowed Origins

```shell
export ADYEN_API_KEY=yourAdyenApiKey
export ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
export ADYEN_HMAC_KEY=yourHmacKey
```

On Windows CMD you can use below commands instead

```shell
set ADYEN_API_KEY=yourAdyenApiKey
set ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
set ADYEN_HMAC_KEY=yourHmacKey
```

2. Start the server:

```
./gradlew bootRun
```

3. Visit [http://localhost:8080/](http://localhost:8080/) to select an integration type.

To try out integrations with test card numbers and payment method details to complete payment on generated links, see [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers).

## License

MIT license. For more information, see the **LICENSE** file in the root directory.
