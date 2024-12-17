package com.adyen.checkout.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Value("${server.port}")
    private int serverPort;

    @Value("${ADYEN_API_KEY:#{null}}")
    private String adyenApiKey;

    @Value("${ADYEN_MERCHANT_ACCOUNT:#{null}}")
    private String adyenMerchantAccount;

    @Value("${ADYEN_CLIENT_KEY:#{null}}")
    private String adyenClientKey;

    @Value("${ADYEN_HMAC_KEY:#{null}}")
    private String adyenHmacKey;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getAdyenApiKey() {
        return adyenApiKey;
    }

    public void setAdyenApiKey(String adyenApiKey) {
        this.adyenApiKey = adyenApiKey;
    }

    public String getAdyenMerchantAccount() {
        return adyenMerchantAccount;
    }

    public void setAdyenMerchantAccount(String adyenMerchantAccount) {
        this.adyenMerchantAccount = adyenMerchantAccount;
    }

    public String getAdyenClientKey() {
        return adyenClientKey;
    }

    public void setAdyenClientKey(String adyenClientKey) {
        this.adyenClientKey = adyenClientKey;
    }

    public String getAdyenHmacKey() {
        return adyenHmacKey;
    }

    public void setAdyenHmacKey(String adyenHmacKey) {
        this.adyenHmacKey = adyenHmacKey;
    }
}
