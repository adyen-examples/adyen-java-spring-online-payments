package com.adyen.checkout.zeroconfig;

public class ZeroConfigData {

    private String id;
    private String merchantAccount;
    private String clientKey;
    private String apiKey;
    private String webhookId;
    private String webhookUrl;
    private String hmacKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getHmacKey() {
        return hmacKey;
    }

    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }

    @Override
    public String toString() {
        return "ZeroConfigData{" +
                "id='" + id + '\'' +
                ", merchantAccount='" + merchantAccount + '\'' +
                ", clientKey='" + clientKey + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", webhookId='" + webhookId + '\'' +
                ", webhookUrl='" + webhookUrl + '\'' +
                ", hmacKey='" + hmacKey + '\'' +
                '}';
    }
}
