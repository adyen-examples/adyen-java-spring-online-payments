package com.adyen.checkout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationProperty {

    @Value("${server.port}")
    private int serverPort;

    @Value("${ADYEN_API_KEY:#{null}}")
    private String apiKey;

    @Value("${ADYEN_MERCHANT_ACCOUNT:#{null}}")
    private String merchantAccount;

    @Value("${ADYEN_CLIENT_KEY:#{null}}")
    private String clientKey;

    @Value("${ADYEN_HMAC_KEY:#{null}}")
    private String hmacKey;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

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

    public String getHmacKey() {
        return hmacKey;
    }

    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }
}
