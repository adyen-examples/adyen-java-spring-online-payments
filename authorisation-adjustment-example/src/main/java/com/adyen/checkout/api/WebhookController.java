package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.model.PaymentDetailsModel;
import com.adyen.checkout.util.Storage;
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
import java.time.LocalDateTime;

/**
 * REST controller for receiving Adyen webhook notifications
 */
@RestController
@RequestMapping("/api")
public class WebhookController {
    private final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    public WebhookController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (HMAC signatures cannot be validated when the app receives webhooks)");
            throw new RuntimeException("ADYEN_HMAC_KEY is UNDEFINED");
        }
    }

    /**
     * Process incoming Webhook notification: get NotificationRequestItem, validate HMAC signature,
     * consume the event asynchronously, send response status 202
     *
     * @param json Payload of the webhook event
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
                                    Alias : {}
                                    PSP reference : {}"""
                            , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("alias"), item.getPspReference());

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
            throw new Exception("empty payload");
        }

        // Acknowledge event has been consumed
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();

    }

    // process payload asynchronously
    private void consumeEvent(NotificationRequestItem notification) {
        switch (notification.getEventCode()) {
            case "AUTHORISATION":
                log.info("Payment authorised - pspReference: {} eventCode: {}", notification.getPspReference(), notification.getEventCode());
                savePayment(notification);
                break;

            case "AUTHORISATION_ADJUSTMENT":
                log.info("Authorisation adjustment - pspReference: {} eventCode: {}", notification.getPspReference(), notification.getEventCode());
                savePayment(notification);
                if (notification.isSuccess()) {
                    // for demo purposes, we add 28 days pre-authorisation to the expiry date
                    // the value of '28' varies per scheme, see: https://docs.adyen.com/online-payments/adjust-authorisation/#validity
                    var expiryDate = LocalDateTime.now().plusDays(28);
                    Storage.updatePayment(notification.getMerchantReference(), notification.getAmount().getValue(), expiryDate);
                }
                break;

            case "CAPTURE":
                log.info("Payment capture - pspReference: {} eventCode: {}", notification.getPspReference(), notification.getEventCode());
                savePayment(notification);
                break;

            case "CAPTURE_FAILED":
                log.info("Payment capture failed - pspReference: {} eventCode: {}", notification.getPspReference(), notification.getEventCode());
                savePayment(notification);
                break;

            case "CANCEL_OR_REFUND":
                log.info("Payment cancel_or_refund - pspReference: {} eventCode: {}", notification.getPspReference(), notification.getEventCode());
                savePayment(notification);
                break;

            case "REFUND_FAILED":
                log.info("Payment refund failed - pspReference: {} eventCode: {}", notification.getPspReference(), notification.getEventCode());
                savePayment(notification);
                break;

            case "REFUNDED_REVERSED":
                log.info("Payment refund reversed - pspReference: {} eventCode: {}", notification.getPspReference(), notification.getEventCode());
                savePayment(notification);
                break;

            default:
                log.warn("Unexpected eventCode: {}", notification.getEventCode());
                break;
        }
    }

    private void savePayment(NotificationRequestItem notification) {
        PaymentDetailsModel paymentDetails = new PaymentDetailsModel(
                notification.getMerchantReference(),
                notification.getPspReference(),
                notification.getOriginalReference(),
                notification.getAmount().getValue(),
                notification.getAmount().getCurrency(),
                LocalDateTime.now(),
                notification.getEventCode(),
                notification.getReason(),
                notification.getPaymentMethod(),
                notification.isSuccess()
        );
        Storage.addPaymentToHistory(paymentDetails);
    }

    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
