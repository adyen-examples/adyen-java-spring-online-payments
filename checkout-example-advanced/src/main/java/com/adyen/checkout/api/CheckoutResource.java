package com.adyen.checkout.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import com.adyen.checkout.ApplicationProperty;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final Checkout checkout;

    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.checkout = new Checkout(client);
    }

    /**
     * {@code POST  /getPaymentMethods} : Get valid payment methods.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/getPaymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        var paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        log.info("REST request to get Adyen payment methods {}", paymentMethodsRequest);
        var response = checkout.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    /**
     * {@code POST  /initiatePayment} : Make a payment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/initiatePayment")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody String body, HttpServletRequest request) throws IOException, ApiException {

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(body, JsonObject.class);
        com.google.gson.JsonObject method = obj.getAsJsonObject("paymentMethod");

        PaymentMethod paymentMethod = PaymentMethod.fromJson(method.toString());

        System.out.println(paymentMethod.getType());
        var paymentRequest = PaymentRequest.fromJson(body);
//        Gson gson = new Gson();
//        var paymentRequest = gson.fromJson(body, PaymentRequest.class);
//        var paymentRequest = new PaymentRequest();

        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
            .currency("EUR")
            .value(10000L); // value is 10€ in minor units

        paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount()); // required
        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB); // required
        paymentRequest.setReference(orderRef); // required
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=" + orderRef);

        paymentRequest.setAmount(amount);
        // set lineItems required for some payment methods (ie Klarna)
        paymentRequest.setLineItems(Arrays.asList(
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Sunglasses"),
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Headphones"))
        );
        // required for 3ds2 native flow
        paymentRequest.setAdditionalData(Collections.singletonMap("allow3DS2", "true"));
        // required for 3ds2 native flow
        paymentRequest.setOrigin(request.getScheme() + "://" + host );
        // required for 3ds2
        paymentRequest.setBrowserInfo(paymentRequest.getBrowserInfo());
        // required by some issuers for 3ds2
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setPaymentMethod(paymentRequest.getPaymentMethod());

        log.info("REST request to make Adyen payment {}", paymentRequest);
        var response = checkout.payments(paymentRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    /**
     * {@code POST  /submitAdditionalDetails} : Make a payment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/submitAdditionalDetails")
    public ResponseEntity<PaymentDetailsResponse> payments(@RequestBody DetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("REST request to make Adyen payment details {}", detailsRequest);
        var response = checkout.paymentsDetails(detailsRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    /**
     * {@code GET  /handleShopperRedirect} : Handle redirect during payment.
     *
     * @return the {@link RedirectView} with status {@code 302}
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @GetMapping("/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult, @RequestParam String orderRef) throws IOException, ApiException {
        var detailsRequest = new DetailsRequest();
        PaymentCompletionDetails paymentCompletionDetails = new PaymentCompletionDetails();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            paymentCompletionDetails.setRedirectResult(redirectResult);
        } else if (payload != null && !payload.isEmpty()) {
            paymentCompletionDetails.setPayload(payload);
        }

        detailsRequest.setDetails(paymentCompletionDetails);
        return getRedirectView(detailsRequest);
    }

    private RedirectView getRedirectView(final DetailsRequest detailsRequest) throws ApiException, IOException {
        log.info("REST request to handle payment redirect {}", detailsRequest);
        var response = checkout.paymentsDetails(detailsRequest);
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
}
