package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.util.Storage;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.SignatureException;

/**
 * REST controller for receiving Adyen webhook notifications
 */
@CrossOrigin
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
                    // invalid HMAC signature: do not send [accepted] response
                    log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                    throw new RuntimeException("Invalid HMAC signature");
                }

                log.info("Received webhook success:{} eventCode:{}", item.isSuccess(), item.getEventCode());

                // consume payload
                if(item.isSuccess()) {

                    // Per default, Adyen will send a webhook with the eventCode: "AUTHORISATION".
                    // We recommend changing this behavior in the Customer Area to receive the "RECURRING_CONTRACT" eventCode. 
                    // You'll need to do two things:
                    // (1) Enable the "RECURRING_CONTRACT" eventCode in your Customer Area, under "Developers" -> "Webhooks" -> "Settings". Enable "Recurring contract" on `Merchant`-level and hit "Save".
                    // This will send the event code "RECURRING_CONTRACT" when a recurring contract has been created.
                    // (2) Make sure that your webhook also sends the event "RECURRING_CONTRACT" ("Developers" -> "Webhooks" -> Click on your webhook -> "General").
                    // Read more here: https://docs.adyen.com/online-payments/tokenization/create-and-use-tokens?tab=subscriptions_2#pending-and-refusal-result-codes-1
                    if (item.getEventCode().equals("RECURRING_CONTRACT") && item.getAdditionalData() != null && item.getAdditionalData().get("recurring.shopperReference") != null) {
                        // webhook with recurring token
                        log.info("Recurring authorized - recurringDetailReference {}", item.getAdditionalData().get("recurring.recurringDetailReference"));

                        // save token
                        Storage.add(item.getAdditionalData().get("recurring.recurringDetailReference"), item.getPaymentMethod(), item.getAdditionalData().get("recurring.shopperReference"));
                    } else if (item.getEventCode().equals("AUTHORISATION")) {
                        // webhook with payment authorisation
                        log.info("Payment authorized - PspReference {}", item.getPspReference());
                    } else {
                        // unexpected eventCode
                        log.warn("Unexpected eventCode: " + item.getEventCode());
                    }
                } else {
                    // Operation has failed: check the reason field for failure information.
                    log.info("Operation has failed: " + item.getReason());
                }

            } catch (SignatureException e) {
                // Unexpected error during HMAC validation: do not send [accepted] response
                log.error("Error while validating HMAC Key", e);
                throw new RuntimeException(e.getMessage());
            }

        }

        // Acknowledge event has been consumed
        return ResponseEntity.ok().body("[accepted]");
    }


    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
