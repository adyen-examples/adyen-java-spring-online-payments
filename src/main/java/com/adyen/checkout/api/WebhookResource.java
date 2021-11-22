package com.adyen.checkout.api;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;

/**
 * REST controller for receiving Adyen webhooks
 */
@RestController
@RequestMapping("/api")
public class WebhookResource {
    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    @Value("${ADYEN_HMAC_KEY}")
    private String hmacKey;

    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody NotificationRequest notificationRequest){

//        log.info("RECEIVED WEBHOOK notification {}", notificationRequest);

        notificationRequest.getNotificationItems().forEach(
          item -> {
              try {
                  if (new HMACValidator().validateHMAC(item, hmacKey)) {
                      log.info("Received webhook with event " + item.getEventCode() + " for " + item.getMerchantReference() + " with reference" + item.getOriginalReference());
                  } else {
                      log.warn("Could not validate HMAC Key for incoming webhook message. Have you set the environment variable?");
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
