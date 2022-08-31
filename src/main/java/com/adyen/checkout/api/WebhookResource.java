package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

/**
 * REST controller for receiving Adyen webhook notifications
 */
@RestController
@RequestMapping("/api")
public class WebhookResource {
    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    private ApplicationProperty applicationProperty;

    @Autowired
    public WebhookResource(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (Webhook cannot be authenticated)");
            //throw new RuntimeException("ADYEN_HMAC_KEY is UNDEFINED");
        }
    }

    /**
     * Process incoming Webhook notifications
     * @param notificationRequest
     * @return
     */
    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody NotificationRequest notificationRequest){

        notificationRequest.getNotificationItems().forEach(
          item -> {
              // We recommend validate HMAC signature in the webhooks for security reasons
              try {
                  if (new HMACValidator().validateHMAC(item, this.applicationProperty.getHmacKey())) {
                      log.info("Received webhook with event {} : \n" +
                          "Merchant Reference: {}\n" +
                          "Alias : {}\n" +
                          "PSP reference : {}"
                          , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("alias"), item.getPspReference());
                  } else {
                      // invalid HMAC signature: do not send [accepted] response
                      log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                      throw new RuntimeException("Invalid HMAC signature");
                  }
              } catch (SignatureException e) {
                  log.error("Error while validating HMAC Key", e);
              }
          }
        );

        // Notifying the server we're accepting the payload
        return ResponseEntity.ok().body("[accepted]");
    }
}
