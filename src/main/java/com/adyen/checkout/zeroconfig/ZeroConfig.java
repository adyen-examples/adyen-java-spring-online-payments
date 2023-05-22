package com.adyen.checkout.zeroconfig;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.enums.Environment;
import com.adyen.model.management.*;
import com.adyen.service.exception.ApiException;
import com.adyen.service.management.ApiCredentialsMerchantLevelApi;
import com.adyen.service.management.WebhooksMerchantLevelApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private ApiCredentialsMerchantLevelApi apiCredentialsMerchantLevelApi;
    private WebhooksMerchantLevelApi webhooksMerchantLevelApi;

    @Autowired
    public ZeroConfig(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);

        this.apiCredentialsMerchantLevelApi = new ApiCredentialsMerchantLevelApi(client);
        this.webhooksMerchantLevelApi = new WebhooksMerchantLevelApi(client);
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
        } catch (ApiException e) {
            log.error("An ApiException has occurred: " +  e.getError());
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

        var resp = this.apiCredentialsMerchantLevelApi.createApiCredential(applicationProperty.getMerchantAccount(), req);

        log.info("createApiCredential {}", resp);

        return resp;
    }

    public Webhook createWebhook() throws IOException, ApiException {

        String hostname = InetAddress.getLoopbackAddress().getHostName();
        int port = this.applicationProperty.getServerPort();

        log.info("hostname: " + hostname);
        log.info("port: " + port);

        var req = new CreateMerchantWebhookRequest()
                .description("ZeroConfig")
                .type("standard")
                .active(true)
                .communicationFormat(CreateMerchantWebhookRequest.CommunicationFormatEnum.JSON)
                .username(this.applicationProperty.getWebhookUsername())
                .password(this.applicationProperty.getWebhookPassword())
                .url(this.applicationProperty.getWebhookUrl());

        var resp = this.webhooksMerchantLevelApi.setUpWebhook(applicationProperty.getMerchantAccount(), req);
        log.info("createWebhook {}", resp);

        return resp;

    }

    public GenerateHmacKeyResponse createHmacKey(String webhookId) throws IOException, ApiException {

        var resp = this.webhooksMerchantLevelApi.generateHmacKey(applicationProperty.getMerchantAccount(), webhookId);
        log.info("createHmacKey {}", resp);

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

        String json =  getGson().toJson(data);
        Files.write(Paths.get(ZERO_CONFIG_DATA_FILE), json.getBytes(StandardCharsets.UTF_8));
    }

    private ZeroConfigData readZeroConfigData() throws IOException {
        String json = Files.readString(Paths.get(ZERO_CONFIG_DATA_FILE));

        return getGson().fromJson(json, ZeroConfigData.class);
    }

    private Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }


}
