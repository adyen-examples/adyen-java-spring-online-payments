package com.adyen.ipp.api;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.ipp.ApplicationProperty;
import com.adyen.model.checkout.CreateCheckoutSessionResponse;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class InPersonPaymentsController {
    private final Logger log = LoggerFactory.getLogger(InPersonPaymentsController.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    public InPersonPaymentsController(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
    }

    @PostMapping("/ipp")
    public ResponseEntity sessions(@RequestHeader String host, HttpServletRequest request) throws IOException, ApiException {
        var response = "";
        return ResponseEntity.ok().body(response);
    }
}
