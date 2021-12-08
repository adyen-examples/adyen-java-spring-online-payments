package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.CreateCheckoutSessionRequest;
import com.adyen.model.checkout.CreateCheckoutSessionResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    @Value("${ADYEN_MERCHANT_ACCOUNT}")
    private String merchantAccount;

    @Value("${ADYEN_RETURN_URL:http://localhost:8080}")
    private String returnUrl;

    private final Checkout checkout;

    public CheckoutResource(@Value("${ADYEN_API_KEY}") String apiKey) {
        var client = new Client(apiKey, Environment.TEST);
        this.checkout = new Checkout(client);
    }

    @PostMapping("/sessions")
    public ResponseEntity<CreateCheckoutSessionResponse> sessions(@RequestParam String type) throws IOException, ApiException {
        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
            .currency("EUR")
            .value(1000L); // value is 10€ in minor units

        var checkoutSession = new CreateCheckoutSessionRequest();
        checkoutSession.merchantAccount(merchantAccount);
        checkoutSession.setChannel(CreateCheckoutSessionRequest.ChannelEnum.WEB);
        checkoutSession.setReference(orderRef); // required
        checkoutSession.setReturnUrl(returnUrl + "/redirect?orderRef=" + orderRef);

        checkoutSession.setAmount(amount);

        log.info("REST request to create Adyen Payment Session {}", checkoutSession);
        var response = checkout.sessions(checkoutSession);
        return ResponseEntity.ok().body(response);
    }
}
