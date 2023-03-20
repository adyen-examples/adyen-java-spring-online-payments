docker run \
-e ADYEN_CLIENT_KEY \
-e ADYEN_MERCHANT_ACCOUNT \
-e ADYEN_HMAC_KEY \
-e ADYEN_API_KEY \
-p8080:8080 online-payments-spring-adyen:latest
