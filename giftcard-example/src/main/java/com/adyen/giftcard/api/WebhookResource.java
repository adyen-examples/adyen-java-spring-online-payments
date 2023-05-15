package com.adyen.giftcard.api;

import com.adyen.giftcard.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (Webhook cannot be authenticated)");
            //throw new RuntimeException("ADYEN_HMAC_KEY is UNDEFINED");
        }
    }

    /**
     * Process the incoming Webhook event: get NotificationRequestItem, validate HMAC signature,
     * consume the event asynchronously, send response ["accepted"]
     *
     * @param json Payload of the webhook event
     * @return
     */
    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws IOException {

        // from JSON string to object
        var notificationRequest = NotificationRequest.fromJson(json);

        // fetch first (and only) NotificationRequestItem
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        if (notificationRequestItem.isPresent()) {

            var item = notificationRequestItem.get();

            try {
                if (!getHmacValidator().validateHMAC(item, this.applicationProperty.getHmacKey())) {
                    // invalid HMAC signature: do not send [accepted] response
                    log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                    throw new RuntimeException("Invalid HMAC signature");
                }

                log.info("Received webhook success:{} eventCode:{}", item.isSuccess(), item.getEventCode());

                // save webhook in DB or queue, process then asynchronously

                // consume payload
                if (item.isSuccess()) {
                    if (item.getEventCode().equals("AUTHORISATION")) {

                        log.info("Payment authorized - pspReference: " + item.getPspReference() + " eventCode: " + item.getEventCode());

                    } else if (item.getEventCode().equals("ORDER_OPENED")) {

                        log.info("Order is opened - pspReference: " + item.getPspReference() + " eventCode: " + item.getEventCode());
                    } else if (item.getEventCode().equals("ORDER_CLOSED")) {

                        log.info("Order is closed - pspReference: " + item.getPspReference() + " eventCode: " + item.getEventCode());
                    }
                } else {
                    // Operation has failed: check the reason field for failure information.
                    log.info("Event " + item.getEventCode() + " has failed: " + item.getReason());

                    // looking for order-n-pspReference
                }

            } catch (SignatureException e) {
                // Unexpected error during HMAC validation: do not send [accepted] response
                log.error("Error while validating HMAC Key", e);
            }

        } else {
            // Unexpected event with no payload: do not send [accepted] response
            log.warn("Empty NotificationItem");
        }

        // Acknowledge event has been consumed
        return ResponseEntity.ok().body("[accepted]");
    }

    // process payload asynchronously
    void consumeEvent(NotificationRequestItem item) {
        // add item to DB, queue or different thread

        // example: send to Kafka consumer

        // producer.send(producerRecord);
        // producer.flush();
        // producer.close();

    }

    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
