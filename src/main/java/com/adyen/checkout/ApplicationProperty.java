package com.adyen.checkout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
public class ApplicationProperty {

    @Value("${ADYEN_API_KEY:#{null}}")
    private String apiKey;

    @Value("${ADYEN_MERCHANT_ACCOUNT:#{null}}")
    private String merchantAccount;

    @Value("${ADYEN_CLIENT_KEY:#{null}}")
    private String clientKey;

    @Value("${ADYEN_HMAC_KEY:#{null}}")
    private String hmacKey;

    @Value("${ADYEN_RETURN_URL:http://localhost:8080}")
    private String returnUrl;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getHmacKey() {
        return hmacKey;
    }

    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }
}
