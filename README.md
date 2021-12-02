# Adyen [online payment](https://docs.adyen.com/checkout) integration demos

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/adyen-examples/adyen-java-spring-online-payments)


This repository includes examples of PCI-compliant UI integrations for online payments with Adyen. Within this demo app, you'll find a simplified version of an e-commerce website, complete with commented code to highlight key features and concepts of Adyen's API. Check out the underlying code to see how you can integrate Adyen to give your shoppers the option to pay with their preferred payment methods, all in a seamless checkout experience.

![Card checkout demo](src/main/resources/static/images/cardcheckout.gif)

## Supported Integrations

**Java + Spring Boot + Thymeleaf** demos of the following client-side integrations are currently available in this repository:

-   [Drop-in](https://docs.adyen.com/checkout/drop-in-web)
-   [Component](https://docs.adyen.com/checkout/components-web)
    -   ACH
    -   Alipay
    -   Card (3DS2)
    -   Dotpay
    -   giropay
    -   iDEAL
    -   Klarna (Pay now, Pay later, Slice it)
    -   SOFORT
    -   PayPal

The Demo leverages Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library) | [Docs](https://docs.adyen.com/development-resources/libraries#java)).

## Requirements

-   Java 11
-   Network access to maven central

## Installation

1. Clone this repo:

```
git clone https://github.com/adyen-examples/adyen-java-spring-online-payments.git
```

## Usage

1. Set environment variables for your [API key](https://docs.adyen.com/user-management/how-to-get-the-api-key), [Client Key](https://docs.adyen.com/user-management/client-side-authentication) - Remember to add `http://localhost:8080` as an origin for client key, and merchant account name:

```shell
export ADYEN_API_KEY=yourAdyenApiKey
export ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
export ADYEN_CLIENT_KEY=yourAdyenClientKey
```

On Windows CMD you can use below commands instead

```shell
set ADYEN_API_KEY=yourAdyenApiKey
set ADYEN_MERCHANT_ACCOUNT=yourAdyenMerchantAccount
set ADYEN_CLIENT_KEY=yourAdyenClientKey
```

2. Start the server:

```
./gradlew bootRun
```

3. Visit [http://localhost:8080/](http://localhost:8080/) to select an integration type.

To try out integrations with test card numbers and payment method details, see [Test card numbers](https://docs.adyen.com/development-resources/test-cards/test-card-numbers).

## Testing webhooks

This demo provides simple webhook integration at `/api/webhooks/notifications`. For it to work, you need to:

* Provide a way for Adyen's servers to reach your running application
* Add a standard webhook in your customer area

### Making your server reachable

One possibility is to use a service like [ngrok](https://ngrok.com/product) (which can be used for free). Once you have 
set up ngrok, make sure to add the provided URL to the list of allowed origin in the credentials part of your customer area.

### Setting up a webhook

* In the developers -> webhooks part of the customer area, create a new 'standard notifications' webhook.
* Make sure to check 'Accept self-signed', 'Accept non-trusted root certificates' (test only) and Active.
* In additional settings, add the data you want to receive. A good example is 'Payment Account Reference'.

That's it! Every time you test a new payment method, your server will receive a notification from Adyen's server.

You can find more information about webhooks in [this detailed blog post](https://www.adyen.com/blog/Integrating-webhooks-notifications-with-Adyen-Checkout).

## Deploying this example to the cloud

As part of this example, we are providing a [Terraform](https://www.terraform.io/) configuration file that can be used to deploy this demo to the Amazon cloud on a [Beanstalk](https://aws.amazon.com/elasticbeanstalk/) environment.

 ⚠️ This part will deploy ressource in the cloud and can incur charges ⚠️.


### Extra prerequisites

* The [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html), with a configured profile (and an AWS account).
* The [Terraform](https://www.terraform.io/) CLI, with the `terraform` executable in your PATH.
* Ready to use Adyen API and client keys.

### Usage

* Compile the project: `./gradlew build`
* Create a `terraform.tfvars` file in the root directory of this repository. Here is a example : 

```json
adyen_api_key = "testApiKey"
adyen_merchant_account = "testMerchantAccount"
adyen_client_key = "testClientKey"
adyen_hmac_key = "testHMACKey"
```

* Run the `terraform init` command to initialize the Terraform configuration, and `terraform apply` to deploy the environment.
* At the end of the deployment, Terraform will output several URLs : 

```
adyen_url = "https://ca-test.adyen.com/ca/ca/config/showthirdparty.shtml"
demo_url = "http://adyen-spring-development-cc66dd5f.eu-west-1.elasticbeanstalk.com"
environment_url = "https://eu-west-1.console.aws.amazon.com/elasticbeanstalk/home?region=eu-west-1#/applications"
```

* You can access the demo using the `demo_url`.
* The `adyen_url` can be used to create a [notification webhook](https://docs.adyen.com/development-resources/webhooks) in the Adyen customer area.
* Use `terraform destroy` to remove the environment and avoid being charged for the resources more than necessary.
* The `environment_url` can be used to access the AWS Beanstalk environment and possibly update the configuration.

## Contributing

We commit all our new features directly into our GitHub repository. Feel free to request or suggest new features or code changes yourself as well!

## License

MIT license. For more information, see the **LICENSE** file in the root directory.
