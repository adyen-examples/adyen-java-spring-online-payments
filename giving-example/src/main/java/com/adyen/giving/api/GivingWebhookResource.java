package com.adyen.giving.api;

import com.adyen.giving.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.SignatureException;

/**
 * This is a special REST controller for receiving Adyen webhook notifications related to Giving / Donations
 */
@RestController
@RequestMapping("/api")
public class GivingWebhookResource {
    private final Logger log = LoggerFactory.getLogger(GivingWebhookResource.class);

    private final ApplicationProperty applicationProperty;
    private final HMACValidator getHmacValidator;

    @Autowired
    public GivingWebhookResource(ApplicationProperty applicationProperty, HMACValidator getHmacValidator) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (Webhook cannot be authenticated)");
            //throw new RuntimeException("ADYEN_HMAC_KEY is UNDEFINED");
        }
        this.getHmacValidator = getHmacValidator;
    }


    @PostMapping("/webhooks/giving")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws IOException {


        // from JSON string to object
        var notificationRequest = NotificationRequest.fromJson(json);

        // fetch first (and only) NotificationRequestItem
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

            if (notificationRequestItem.isPresent()) {

                var item = notificationRequestItem.get();

                log.info("""
                        Received webhook with event {} :\s
                        Merchant Account Code: {}
                        PSP reference : {}
                        Donation successful : {}
                        """
                    , item.getEventCode(), item.getMerchantAccountCode(), item.getPspReference(), item.isSuccess());

                // consume event asynchronously
                consumeEvent(item);

        } else {
            // Unexpected event with no payload
            log.warn("Empty NotificationItem");
        }

        // Acknowledge event has been consumed
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
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
    public HMACValidator getGivingHmacValidator() {
        return new HMACValidator();
    }
}
