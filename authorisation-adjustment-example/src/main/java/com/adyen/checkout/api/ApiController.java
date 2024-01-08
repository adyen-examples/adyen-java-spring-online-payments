package com.adyen.checkout.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.model.PaymentModel;
import com.adyen.checkout.util.Storage;
import com.adyen.service.checkout.PaymentsApi;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.exception.ApiException;

/**
 * REST controller for using Adyen Payments API
 */
@RestController
@RequestMapping("/api")
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationProperty applicationProperty;

    private final PaymentsApi paymentsApi;

    public ApiController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
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
        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
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
    @PostMapping("/pre-authorisation")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentRequest();

        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
            .currency("EUR")
            .value(24999L); // value is 249.99€ in minor units

        paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount()); // required
        paymentRequest.setReference(orderRef); // required
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=" + orderRef);

        paymentRequest.setAmount(amount);
        var authenticationData = new AuthenticationData();
        authenticationData.setAttemptAuthentication(AuthenticationData.AttemptAuthenticationEnum.ALWAYS);
        paymentRequest.setAuthenticationData(authenticationData);

        paymentRequest.setAdditionalData(new HashMap<>() { {
            put ("authorisationType", "PreAuth");
        }});

        // required for 3ds2 redirect flow
        paymentRequest.setOrigin(request.getScheme() + "://" + host);
        // required for 3ds2
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        // required by some issuers for 3ds2
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setPaymentMethod(body.getPaymentMethod());
        paymentRequest.setShopperEmail("test@adyen.com");

        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB);
        // we strongly recommend that you the billingAddress in your request
        // card schemes require this for channel web, iOS, and Android implementations
        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setCountry("NL");
        billingAddress.setCity("Amsterdam");
        billingAddress.setStreet("Street");
        billingAddress.setHouseNumberOrName("1");
        billingAddress.setStateOrProvince("North Holland");
        billingAddress.setPostalCode("1000XX");
        paymentRequest.setBillingAddress(billingAddress);

        log.info("REST request to make Adyen payment {}", paymentRequest);
        var response = paymentsApi.payments(paymentRequest);

        if (response.getResultCode() == PaymentResponse.ResultCodeEnum.AUTHORISED) {
            var payment = new PaymentModel(response.getMerchantReference(),
                    response.getPspReference(),
                    response.getAmount().getValue(),
                    response.getAmount().getCurrency(),
                    LocalDateTime.now(),
                    // for demo purposes, we add 28 days pre-authorisation to the expiry date
                    // the value of '28' varies per scheme, see: https://docs.adyen.com/online-payments/adjust-authorisation/#validity
                    LocalDateTime.now().plusDays(28),
                    response.getPaymentMethod().getBrand(),
                    new ArrayList<>()
            );
            Storage.put(payment);
        }
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
    public ResponseEntity<PaymentDetailsResponse> payments(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("REST request to make Adyen payment details {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
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
        var detailsRequest = new PaymentDetailsRequest();

        PaymentCompletionDetails details = new PaymentCompletionDetails();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            // for redirect, you are redirected to an Adyen domain to complete the 3DS2 challenge
            // after completing the 3DS2 challenge, you get the redirect result from Adyen in the returnUrl
            // we then pass on the redirectResult
            details.redirectResult(redirectResult);
        } else if (payload != null && !payload.isEmpty()) {
            details.payload(payload);
        }

        detailsRequest.setDetails(details);
        return getRedirectView(detailsRequest);
    }

    private RedirectView getRedirectView(final PaymentDetailsRequest detailsRequest) throws ApiException, IOException {
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
}
