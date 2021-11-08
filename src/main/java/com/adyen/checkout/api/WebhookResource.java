package com.adyen.checkout.api;

import com.adyen.model.notification.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for receiving Adyen webhooks
 */
@RestController
@RequestMapping("/api")
public class WebhookResource {

    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody NotificationRequest notificationRequest){

        log.info("RECEIVED WEBHOOK notification {}", notificationRequest);

        // Notifying the server we're accepting the payload
        return ResponseEntity.ok().body("[accepted]");
    }
}
