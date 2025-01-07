package com.adyen.checkout.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import com.adyen.checkout.configurations.ApplicationConfiguration;
import com.adyen.model.RequestOptions;
import com.adyen.service.checkout.PaymentsApi;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.adyen.model.checkout.*;
import com.adyen.service.exception.ApiException;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
public class CheckoutController {
    private final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    private final ApplicationConfiguration applicationConfiguration;

    private final PaymentsApi paymentsApi;

    public CheckoutController(PaymentsApi paymentsApi, ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.paymentsApi = paymentsApi;
    }

    /**
     * {@code POST  /api/paymentMethods}: Retrieves available payment methods from Adyen.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/api/paymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        PaymentMethodsRequest paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(this.applicationConfiguration.getAdyenMerchantAccount());
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        log.info("Sending request to /paymentMethods");
        PaymentMethodsResponse response = paymentsApi.paymentMethods(paymentMethodsRequest);
        log.info("Adyen: /paymentMethods response: {}", response);
        return ResponseEntity.ok().body(response);
    }

    /**
     * {@code POST  /api/payments}: Make a payment request to Adyen.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        PaymentRequest paymentRequest = new PaymentRequest();

        Amount amount = new Amount()
            .currency("EUR")
            .value(10000L); // value is 100€ in minor units, hardcoded in the example, total amount should be retrieved from a storage, e.g. database.

        // Sets ADYEN_MERCHANT_ACCOUNT in the request
        paymentRequest.setMerchantAccount(this.applicationConfiguration.getAdyenMerchantAccount());
        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB);
        paymentRequest.setReference(UUID.randomUUID().toString()); // required
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect"); // Redirect flow

        paymentRequest.setAmount(amount);

        // Set countryCode and lineItems fields, which is required for some payment methods (e.g. Klarna)
        paymentRequest.setCountryCode("NL");
        paymentRequest.setLineItems(Arrays.asList(
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Sunglasses"),
            new LineItem().quantity(1L).amountIncludingTax(5000L).description("Headphones"))
        );

        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setAttemptAuthentication(AuthenticationData.AttemptAuthenticationEnum.ALWAYS);
        // Add the following lines for Native 3DS2:
        //ThreeDSRequestData threeDSRequestData = new ThreeDSRequestData();
        //threeDSRequestData.setNativeThreeDS(ThreeDSRequestData.NativeThreeDSEnum.PREFERRED);
        //authenticationData.setThreeDSRequestData(threeDSRequestData);

        paymentRequest.setAuthenticationData(authenticationData);

        // Required for authentication - 3DS2 redirect flow
        paymentRequest.setOrigin(request.getScheme() + "://" + host);
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        // we strongly recommend that you the billingAddress in your request
        // card schemes require this for channel web, iOS, and Android implementations
        /*paymentRequest.setBillingAddress(new BillingAddress()
            .city("Amsterdam")
            .country("NL")
            .postalCode("1234AA")
            .street("Street 1")
            .houseNumberOrName("1"));
         */

         // Sets the shopper email
         //paymentRequest.setShopperEmail("youremail@email.com");

        // https://docs.adyen.com/development-resources/api-idempotency/
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setIdempotencyKey(UUID.randomUUID().toString());

        log.info("Sending request to /payments");
        PaymentResponse paymentResponse = paymentsApi.payments(paymentRequest, requestOptions);
        log.info("Adyen: /payments response: {}", paymentRequest);
        return ResponseEntity.ok().body(paymentResponse);
    }

    /**
     * {@code POST  /api/payments/details} : Make a payments/details request to Adyen.
     *
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/api/payments/details")
    public ResponseEntity<PaymentDetailsResponse> paymentsDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("Sending request to /payments/details {}", detailsRequest);
        PaymentDetailsResponse detailsResponse = paymentsApi.paymentsDetails(detailsRequest);
        log.info("Adyen: /payments/details response: {}", detailsResponse);
        return ResponseEntity.ok().body(detailsResponse);
    }

    /**
     * {@code GET  /api/handleShopperRedirect} : Handle redirect during payment. This gets called during the redirect flow, you can specify this in the `returnUrl`-field when constructing the /payments request to Adyen.
     *
     * @return the {@link RedirectView} with status {@code 302}
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @GetMapping("/api/handleShopperRedirect")
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
