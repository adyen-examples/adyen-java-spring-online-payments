package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;
import java.util.Optional;

/**
 * REST controller for receiving Adyen webhook notifications
 */
@RestController
public class WebhookController {
    private final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final ApplicationProperty applicationProperty;
    private final HMACValidator hmacValidator;

    @Autowired
    public WebhookController(ApplicationProperty applicationProperty, HMACValidator hmacValidator) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (Webhook cannot be authenticated)");
            //throw new RuntimeException("ADYEN_HMAC_KEY is UNDEFINED");
        }
        this.hmacValidator = hmacValidator;
    }

    /**
     * Process incoming Webhook notification: get NotificationRequestItem, validate HMAC signature
     * Consume the webhook asynchronously, send response status 202: Accepted
     * Best practice: Add authentication to protect this endpoint, see https://docs.adyen.com/development-resources/webhooks/
     * @param json The payload of the incoming webhook
     * @return 202 "Accepted" - If your server has finished processing the payload (e.g. storing it).
     */
    @PostMapping("/api/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws Exception {
        log.warn("Incoming (not validated) webhook {}", json);
        // From JSON string to object
        NotificationRequest notificationRequest = NotificationRequest.fromJson(json);

        // Fetch first (and only) NotificationRequestItem
        Optional<NotificationRequestItem> notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        if (notificationRequestItem.isEmpty()) {
            // Unexpected webhook with no payload
            log.warn("Empty NotificationItem");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty NotificationItem");
        }

        try {
            NotificationRequestItem item = notificationRequestItem.get();
            if (hmacValidator.validateHMAC(item, this.applicationProperty.getHmacKey())) {
                log.info("""
                                Received webhook with event {} :\s
                                Merchant Reference: {}
                                Alias : {}
                                PSP reference : {}"""
                        , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("alias"), item.getPspReference());

                // Consume event asynchronously
                consumeEvent(item);

            } else {
                // Invalid HMAC signature
                log.warn("Could HMAC signature did not match: {}", item);
                throw new RuntimeException("HMAC signature did not match");
            }
        } catch (SignatureException e) {
            // Unexpected error during HMAC validation
            log.error("Error while validating HMAC Key", e);
            throw new SignatureException(e);
        }

        // Acknowledge event has been consumed
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // process payload asynchronously
    void consumeEvent(NotificationRequestItem item) {
        log.info("Consumed webhook {}", item.toString());

        // add item to DB, queue or different thread

        // example: send to Kafka consumer

        // producer.send(producerRecord);
        // producer.flush();
        // producer.close();
    }
}
