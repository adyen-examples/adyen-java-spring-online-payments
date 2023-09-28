# Adyen [Giving](https://docs.adyen.com/online-payments/donations/) (donations) Integration Demo

## Run this integration in seconds using [Gitpod](https://gitpod.io/)

* Open your [Adyen Test Account](https://ca-test.adyen.com/ca/ca/overview/default.shtml) and create a set of [API keys](https://docs.adyen.com/user-management/how-to-get-the-api-key).
* Go to [gitpod account variables](https://gitpod.io/variables).
* Set the `ADYEN_API_KEY`, `ADYEN_CLIENT_KEY`, `ADYEN_HMAC_KEY` and `ADYEN_MERCHANT_ACCOUNT` variables.
* Click the button below!

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spring-online-payments/tree/main/giving-example)

_NOTE: To allow the Adyen Drop-In and Components to load, you have to add `https://*.gitpod.io` as allowed origin for your chosen set of [API Credentials](https://ca-test.adyen.com/ca/ca/config/api_credentials_new.shtml)_

## Details

This is a sample designed to demonstrate the Adyen Giving donations workflow.
First make a test payment using one of our test card numbers, so you can see the donation screen appear.

![Giving demo](src/main/resources/static/images/donations.gif)

> **Note**: You need to have donations [enabled in your Customer Area](https://docs.adyen.com/online-payments/donations/testing/#step-1-enable-the-donation-token-in-your-api-response) in order to receive the `DonationToken`.

## Supported Integrations

The amount of payment methods supported for donations is limited. In this demo, you will find a use case centered around the use of cards. 
The Demo leverages Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library) | [Docs](https://docs.adyen.com/development-resources/libraries?tab=java_2#java)).

## Requirements

-   Java 17
-   Network access to maven central

## Installation

1. Clone this repo:

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```

## Usage

1. Set environment variables for the required configuration
    - [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key)
    - [Client Key](https://docs.adyen.com/user-management/client-side-authentication)
    - [Merchant Account](https://docs.adyen.com/account/account-structure)
    - [HMAC Key](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures)

Remember to include `http://localhost:8080` in the list of Allowed Origins

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
2. Start the server:

```
./gradlew bootRun
```

3. Visit [http://localhost:8080/](http://localhost:8080/) pick the Card component integration, follow the instructions, and after the payment you will be offered to make a donation.

To try out this application with test card numbers, visit [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers).
We recommend saving some test cards in your browser so you can test your integration faster in the future.

## Testing webhooks

Webhooks deliver asynchronous notifications and it is important to test them during the setup of your integration. You can find more information about webhooks in [this detailed blog post](https://www.adyen.com/blog/Integrating-webhooks-notifications-with-Adyen-Checkout).

This sample application provides two webhook integrations exposed at `/api/webhooks/notifications` and `/api/webhooks/giving`, respectively to receive payment notifications and donation notifications. For it to work, you need to:

1. Provide a way for the Adyen platform to reach your running application
2. Add a Standard webhook in your Customer Area

### Making your server reachable

Your endpoint that will consume the incoming webhook must be publicly accessible.

There are typically 3 options:
* deploy on your own cloud provider
* deploy on Gitpod
* expose your localhost with tunneling software (i.e. ngrok)

#### Option 1: cloud deployment
If you deploy on your cloud provider (or your own public server) the webhook URLs will be the URL of the server 
```
  https://{cloud-provider}/api/webhooks/notifications
  https://{cloud-provider}/api/webhooks/giving
```

#### Option 2: Gitpod
If you use Gitpod the webhook URL will be the host assigned by Gitpod
```
  https://myorg-myrepo-y8ad7pso0w5.ws-eu75.gitpod.io/api/webhooks/notifications
  https://myorg-myrepo-y8ad7pso0w5.ws-eu75.gitpod.io/api/webhooks/giving
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

