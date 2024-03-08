package com.adyen.ipp.api;

import com.adyen.ipp.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.SignatureException;

/**
 * REST controller for receiving Adyen webhook notifications
 */
@RestController
@RequestMapping("/api")
public class WebhookController {
    private final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private ApplicationProperty applicationProperty;

    @Autowired
    public WebhookController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (Webhook cannot be authenticated)");
        }
    }

    /**
     * Process the incoming Webhook event: get NotificationRequestItem, validate HMAC signature,
     * consume the event asynchronously, send response status 202
     *
     *  @param json Payload of the webhook event
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
                    // invalid HMAC signature
                    log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                    throw new RuntimeException("Invalid HMAC signature");
                }

                log.info("Received webhook success:{} eventCode:{}", item.isSuccess(), item.getEventCode());

                // consume payload
                if(item.isSuccess()) {
                    if (item.getEventCode().equals("AUTHORISATION")) {
                        // webhook with recurring token
                        log.info("Webhook AUTHORISATION - PspReference {}", item.getPspReference());
                    } else if (item.getEventCode().equals("CANCEL_OR_REFUND")) {
                        // webhook with payment authorisation
                        log.info("Webhook CANCEL_OR_REFUND - PspReference {}", item.getPspReference());
                    } else if (item.getEventCode().equals("REFUND_FAILED")) {
                        // webhook with payment authorisation
                        log.info("Webhook REFUND_FAILED - PspReference {}", item.getPspReference());
                    } else if (item.getEventCode().equals("REFUNDED_REVERSED")) {
                        // webhook with payment authorisation
                        log.info("Webhook REFUNDED_REVERSED - PspReference {}", item.getPspReference());
                    } else {
                        // unexpected eventCode
                        log.warn("Unexpected eventCode: " + item.getEventCode());
                    }
                } else {
                    // Operation has failed: check the reason field for failure information.
                    log.info("Operation has failed: " + item.getReason());
                }

            } catch (SignatureException e) {
                // Unexpected error during HMAC validation
                log.error("Error while validating HMAC Key", e);
                throw new RuntimeException(e.getMessage());
            }

        }

        // Acknowledge event has been consumed
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
