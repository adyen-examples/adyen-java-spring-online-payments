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
import java.util.Collections;
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


    @PostMapping("/getPaymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        var paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        log.info("REST request to get Adyen payment methods {}", paymentMethodsRequest);
        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    @PostMapping("/submitAdditionalDetails")
    public ResponseEntity<PaymentDetailsResponse> payments(@RequestBody DetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("REST request to make Adyen payment details {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    @GetMapping("/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult, @RequestParam String orderRef) throws IOException, ApiException {
        var detailsRequest = new DetailsRequest();
        PaymentCompletionDetails paymentCompletionDetails = new PaymentCompletionDetails();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            paymentCompletionDetails.setRedirectResult(redirectResult);
            detailsRequest.setDetails(paymentCompletionDetails);
        } else if (payload != null && !payload.isEmpty()) {
            paymentCompletionDetails.setPayload(redirectResult);
            detailsRequest.setDetails(paymentCompletionDetails);
        }

        return getRedirectView(detailsRequest);
    }

    private RedirectView getRedirectView(final DetailsRequest detailsRequest) throws ApiException, IOException {
        log.info("REST request to handle payment redirect {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
        var redirectURL = "/result/";
        switch (response.getResultCode()) {
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
        return new RedirectView(redirectURL + "?reason=" + response.getResultCode());
    }

    @PostMapping("/initiatePayment")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount()); // required
        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB); // required

        var amount = new Amount()
            .currency("EUR")
            .value(1000L); // value is 10€ in minor units
        paymentRequest.setAmount(amount);
        paymentRequest.setCountryCode("NL");

        var orderRef = UUID.randomUUID().toString();
        paymentRequest.setReference(orderRef); // required
        // required for 3ds2 redirect flow
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/redirect?orderRef=" + orderRef);

        // required for 3ds2 native flow
        paymentRequest.setAdditionalData(Collections.singletonMap("allow3DS2", "true"));
        // required for 3ds2 native flow
        paymentRequest.setOrigin("http://localhost:8080");
        // required for 3ds2
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        // required by some issuers for 3ds2
        paymentRequest.setShopperIP(request.getRemoteAddr());

        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        paymentRequest.setLineItems(Arrays.asList(
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Sunglasses"),
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Headphones"))
        );

        var type = body.getPaymentMethod().getSchemaType();

        log.info("REST request to make Adyen payment {}", paymentRequest);
        var response = paymentsApi.payments(paymentRequest);
        return ResponseEntity.ok()
            .body(response);
    }
}
