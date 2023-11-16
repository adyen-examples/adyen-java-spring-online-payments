package com.adyen.checkout.api;

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

    private final PaymentsApi paymentsApi;

    @Autowired
    public SubscriptionResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }
        
        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
    }

    @PostMapping("/tokenization/sessions")
    public ResponseEntity<CreateCheckoutSessionResponse> sessions(@RequestHeader String host, HttpServletRequest request) throws IOException, ApiException {
        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
            .currency("EUR")
            .value(0L); // zero-auth transaction

        var checkoutSession = new CreateCheckoutSessionRequest();
        checkoutSession.setAmount(amount);
        checkoutSession.countryCode("NL");
        checkoutSession.merchantAccount(this.applicationProperty.getMerchantAccount());
        checkoutSession.setReference(orderRef); // required
        checkoutSession.setShopperReference(Storage.SHOPPER_REFERENCE); // required
        checkoutSession.setChannel(CreateCheckoutSessionRequest.ChannelEnum.WEB);
        checkoutSession.setReturnUrl(request.getScheme() + "://" + host + "/redirect?orderRef=" + orderRef);
        // recurring payment settings
        checkoutSession.setShopperInteraction(CreateCheckoutSessionRequest.ShopperInteractionEnum.ECOMMERCE);
        checkoutSession.setRecurringProcessingModel(CreateCheckoutSessionRequest.RecurringProcessingModelEnum.SUBSCRIPTION);
        checkoutSession.setEnableRecurring(true);

        log.info("/tokenization/sessions {}", checkoutSession);
        var response = paymentsApi.sessions(checkoutSession);
        return ResponseEntity.ok().body(response);
    }
}
