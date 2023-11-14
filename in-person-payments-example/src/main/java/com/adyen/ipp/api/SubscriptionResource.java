package com.adyen.ipp.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.util.Storage;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.Amount;
import com.adyen.model.checkout.CreateCheckoutSessionRequest;
import com.adyen.model.checkout.CreateCheckoutSessionResponse;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class SubscriptionResource {
    private final Logger log = LoggerFactory.getLogger(SubscriptionResource.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    public SubscriptionResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
    }

    @PostMapping("/ipp")
    public ResponseEntity<CreateCheckoutSessionResponse> sessions(@RequestHeader String host, HttpServletRequest request) throws IOException, ApiException {
        var response = "";
        return ResponseEntity.ok().body(response);
    }
}
