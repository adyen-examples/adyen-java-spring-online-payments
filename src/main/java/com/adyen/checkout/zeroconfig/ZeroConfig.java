package com.adyen.checkout.zeroconfig;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.enums.Environment;
import com.adyen.model.management.*;
import com.adyen.service.exception.ApiException;
import com.adyen.service.management.ApiCredentialsMerchantLevel;
import com.adyen.service.management.WebhooksMerchantLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ZeroConfig {

    private final Logger log = LoggerFactory.getLogger(ZeroConfig.class);

    public final static String ZERO_CONFIG_DATA_FILE = "zeroconfig.json";

    private final ApplicationProperty applicationProperty;
    private ApiCredentialsMerchantLevel credentialsMerchantLevel;
    private WebhooksMerchantLevel webhooksMerchantLevel;

    @Autowired
    public ZeroConfig(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);

        this.credentialsMerchantLevel = new ApiCredentialsMerchantLevel(client);
        this.webhooksMerchantLevel = new WebhooksMerchantLevel(client);
    }

    public void init() {

        try {
            if (new File(ZERO_CONFIG_DATA_FILE).exists()) {
                log.info("Load from {}", ZERO_CONFIG_DATA_FILE);
                ZeroConfigData data = readZeroConfigData();

                this.applicationProperty.setClientKey(data.getClientKey());
                this.applicationProperty.setApiKey(data.getApiKey());
                this.applicationProperty.setHmacKey(data.getHmacKey());

                // Local address

                // Remote address

            } else {
                log.info("Creating {}", ZERO_CONFIG_DATA_FILE);

                ZeroConfigData data = new ZeroConfigData();
                data.setMerchantAccount(this.applicationProperty.getMerchantAccount());

                // create new API Credential
                var respCreateApiCredential = createApiCredential();
                storeInZeroConfigData(data, respCreateApiCredential);

                // create new Webhook
                var respCreateWebhook = createWebhook();
                storeInZeroConfigData(data, respCreateWebhook);

                // generate HMAC key
                var respCreateHmacKey = createHmacKey(respCreateWebhook.getId());
                storeInZeroConfigData(data, respCreateHmacKey);

                // create json file
                saveZeroConfigData(data);

            }
        } catch (Exception e) {
            log.error("An error has occurred", e);
        }

    }

    public CreateApiCredentialResponse createApiCredential() throws IOException, ApiException {

        String hostname = InetAddress.getLoopbackAddress().getHostName();
        int port = this.applicationProperty.getServerPort();

        var req = new CreateMerchantApiCredentialRequest()
                .description("ZeroConfig")
                .addAllowedOriginsItem("http://" + hostname + ":" + port)
                .addAllowedOriginsItem("https://" + hostname + ":" + port);

        var resp = this.credentialsMerchantLevel.createApiCredential(applicationProperty.getMerchantAccount(), req);

        return resp;
    }

    public Webhook createWebhook() throws IOException, ApiException {

        String hostname = InetAddress.getLoopbackAddress().getHostName();
        int port = this.applicationProperty.getServerPort();

        var req = new CreateMerchantWebhookRequest()
                .description("ZeroConfig")
                .type("standard")
                .active(true)
                .communicationFormat(CreateMerchantWebhookRequest.CommunicationFormatEnum.json)
                .username(this.applicationProperty.getWebhookUsername())
                .password(this.applicationProperty.getWebhookPassword())
                .url(this.applicationProperty.getWebhookUrl());

        var resp = this.webhooksMerchantLevel.setUpWebhook(applicationProperty.getMerchantAccount(), req);

        return resp;

    }

    public GenerateHmacKeyResponse createHmacKey(String webhookId) throws IOException, ApiException {

        var resp = this.webhooksMerchantLevel.generateHmacKey(applicationProperty.getMerchantAccount(), webhookId);

        return resp;
    }

    private void storeInZeroConfigData(ZeroConfigData data, CreateApiCredentialResponse resp) {
        data.setId(resp.getId());
        data.setApiKey(resp.getApiKey());
        data.setClientKey(resp.getClientKey());
    }

    private void storeInZeroConfigData(ZeroConfigData data, Webhook resp) {
        data.setWebhookId(resp.getId());
        data.setWebhookUrl(resp.getUrl());
    }

    private void storeInZeroConfigData(ZeroConfigData data, GenerateHmacKeyResponse resp) {
        data.setHmacKey(resp.getHmacKey());
    }

    private  void saveZeroConfigData(ZeroConfigData data) throws IOException {
        log.info("Saving " + ZERO_CONFIG_DATA_FILE);

        String json =  JSON.getGson().toJson(data);
        Files.write(Paths.get(ZERO_CONFIG_DATA_FILE), json.getBytes(StandardCharsets.UTF_8));
    }

    private ZeroConfigData readZeroConfigData() throws IOException {
        String json = Files.readString(Paths.get(ZERO_CONFIG_DATA_FILE));

        return JSON.getGson().fromJson(json, ZeroConfigData.class);
    }


}
