package com.adyen.checkout.api;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for receiving Adyen webhooks
 */
@RestController
@RequestMapping("/api")
public class WebhookResource {
    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    @Value("${ADYEN_HMAC_KEY:#{null}}")
    private String hmacKey;

    private final List<NotificationRequestItem> notificationItems = new ArrayList<>();

    // Providing an endpoint to show all received webhooks. You don't want to do that on production
    @GetMapping("webhooks")
    public List<NotificationRequestItem> getItems(){
        return notificationItems;
    }

    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody NotificationRequest notificationRequest){

        notificationRequest.getNotificationItems().forEach(
          item -> {
              notificationItems.add(item); // Populating the view list

              // We recommend to activate HMAC validation in the webhooks for security reasons
//              try {
//                  if (new HMACValidator().validateHMAC(item, hmacKey)) {
                      log.info("Received webhook with event {} : \n" +
                          "Merchant Reference: {}\n" +
                          "Alias : {}\n" +
                          "PSP reference : {}"
                          , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("alias"), item.getPspReference());
//                  } else {
//                      log.warn("Could not validate HMAC Key for incoming webhook message. Have you set the environment variable?");
//                  }
//              } catch (SignatureException e) {
//                  log.error("Error while validating HMAC Key", e);
//              }
          }
        );

        // Notifying the server we're accepting the payload
        return ResponseEntity.ok().body("[accepted]");
    }
}
