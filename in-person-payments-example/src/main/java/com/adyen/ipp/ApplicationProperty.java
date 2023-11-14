package com.adyen.checkout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationProperty {

    @Value("${server.port}")
    private int serverPort;

    @Value("${ADYEN_API_KEY:#{null}}")
    private String apiKey;

    @Value("${ADYEN_HMAC_KEY:#{null}}")
    private String hmacKey;

    @Value("${ADYEN_POS_POI_ID:#{null}}")
    private String poiId;

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

    public String getHmacKey() {
        return hmacKey;
    }

    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }

    public String getPoiId() {  return poiId; }

    public void setPoiId(String poiId) {  this.poiId = poiId;   }
}
