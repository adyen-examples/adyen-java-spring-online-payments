package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final PaymentsApi paymentsApi;

    @Autowired
    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
    }

    @PostMapping("/sessions")
    public ResponseEntity<CreateCheckoutSessionResponse> sessions(@RequestHeader String host, HttpServletRequest request) throws IOException, ApiException {
        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
            .currency("EUR")
            .value(10000L); // value is 100€ in minor units

        var checkoutSession = new CreateCheckoutSessionRequest();
        checkoutSession.countryCode("NL");
        checkoutSession.merchantAccount(this.applicationProperty.getMerchantAccount());
        // (optional) set WEB to filter out payment methods available only for this platform
        checkoutSession.setChannel(CreateCheckoutSessionRequest.ChannelEnum.WEB);
        checkoutSession.setReference(orderRef); // required
        checkoutSession.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect"); // Redirect flow

        checkoutSession.setAmount(amount);
        // set lineItems required for some payment methods (ie Klarna)
        checkoutSession.setLineItems(Arrays.asList(
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Sunglasses"),
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Headphones"))
        );

        log.info("REST request to create Adyen Payment Session {}", checkoutSession);
        var response = paymentsApi.sessions(checkoutSession);
        return ResponseEntity.ok().body(response);
    }

    /**
     * {@code GET  /api/handleShopperRedirect} : Handle redirect during payment. This gets called during the redirect flow, you can specify this in the `returnUrl`-field when constructing the /payments request to Adyen.
     *
     * @return the {@link RedirectView} with status {@code 302}
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @GetMapping("/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult) throws IOException, ApiException {
        // Sends a request to /payments/completionDetails when redirect occurs
        PaymentCompletionDetails completionDetails = new PaymentCompletionDetails();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            // For redirect, you are redirected to an Adyen domain to complete the 3DS2 challenge
            // After completing the 3DS2 challenge, you get the redirect result from Adyen in the returnUrl
            // We then pass the redirectResult to the /completionDetails call to Adyen.
            completionDetails.redirectResult(redirectResult);
        } else if (payload != null && !payload.isEmpty()) {
            completionDetails.payload(payload);
        }

        PaymentDetailsRequest detailsRequest = new PaymentDetailsRequest();
        detailsRequest.setDetails(completionDetails);

        log.info("Sending request to /payments/details {}", detailsRequest);
        PaymentDetailsResponse paymentDetailsResponse = paymentsApi.paymentsDetails(detailsRequest);
        log.info("Adyen: /payments/details response: {}", paymentDetailsResponse);

        // Handle redirect after getting the /payment/completionDetails paymentDetailsResponse
        String redirectURL = "/result/";
        switch (paymentDetailsResponse.getResultCode()) {
            case AUTHORISED:
                redirectURL += "success";
                break;
            case PENDING:
            case RECEIVED:
                redirectURL += "pending";
                break;
            case REFUSED:
                redirectURL += "failed";
                break;
            default:
                redirectURL += "error";
                break;
        }
        return new RedirectView(redirectURL + "?reason=" + paymentDetailsResponse.getResultCode());
    }

}
