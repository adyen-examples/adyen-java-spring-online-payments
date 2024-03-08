package com.adyen.paybylink.api;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.paybylink.ApplicationProperty;
import com.adyen.paybylink.service.PaymentLinkService;
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

@RestController
@RequestMapping("/api")
public class WebhookController {
    private final Logger log = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private PaymentLinkService paymentLinkService;

    private final ApplicationProperty applicationProperty;

    @Autowired
    public WebhookController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (Webhook cannot be authenticated)");
            //throw new RuntimeException("ADYEN_HMAC_KEY is UNDEFINED");
        }
    }

    /** Process incoming Webhook event: get NotificationRequestItem, validate HMAC signature,
     * Process the incoming Webhook: get NotificationRequestItem, validate HMAC signature,
     * consume the event asynchronously, send response status 202
     *
     *  @param json Payload of the webhook event
     * @return
         */
    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws Exception {

        // from JSON string to object
        var notificationRequest = NotificationRequest.fromJson(json);

        // fetch first (and only) NotificationRequestItem
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        if (notificationRequestItem.isPresent()) {
            var item = notificationRequestItem.get();

            try {
                if (getHmacValidator().validateHMAC(item, this.applicationProperty.getHmacKey())) {
                    log.info("""
                            Received webhook with event {} :\s
                            Merchant Reference: {}
                            PaymentLinkId : {}
                            PSP reference : {}"""
                            , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("paymentLinkId"), item.getPspReference());

                    // consume event asynchronously
                    consumeEvent(item);

                } else {
                    // invalid HMAC signature
                    log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                    throw new RuntimeException("Invalid HMAC signature");
                }
            } catch (SignatureException e) {
                // Unexpected error during HMAC validation
                log.error("Error while validating HMAC Key", e);
                throw new SignatureException(e);
            }

        } else {
            // Unexpected event with no payload
            log.warn("Empty NotificationItem");
            throw new Exception("empty");
        }

        // Acknowledge event has been consumed
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    // process payload asynchronously
    void consumeEvent(NotificationRequestItem item) {

        if (item.getAdditionalData() != null
            && item.getAdditionalData().get("paymentLinkId") != null
            && !item.getAdditionalData().get("paymentLinkId").isEmpty()) {
            System.out.println("Requesting update for link with ID: " + item.getAdditionalData().get("paymentLinkId"));
            paymentLinkService.updateLink(item.getAdditionalData().get("paymentLinkId"));
        }
        else{
            System.out.println("No paymentLinkId found in webhook payload");
        }

    }

    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
