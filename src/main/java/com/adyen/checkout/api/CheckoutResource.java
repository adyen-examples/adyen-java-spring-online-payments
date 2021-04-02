package com.adyen.checkout.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
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

    @Value("${ADYEN_MERCHANT_ACCOUNT}")
    private String merchantAccount;

    private final Checkout checkout;

    public CheckoutResource(@Value("${ADYEN_API_KEY}") String apiKey) {
        var client = new Client(apiKey, Environment.TEST);
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
        paymentMethodsRequest.setMerchantAccount(merchantAccount);
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
    public ResponseEntity<PaymentsResponse> payments(@RequestBody PaymentsRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentsRequest();
        paymentRequest.setMerchantAccount(merchantAccount); // required
        paymentRequest.setChannel(PaymentsRequest.ChannelEnum.WEB); // required

        var amount = new Amount()
            .currency(findCurrency(body.getPaymentMethod().getType()))
            .value(1000L); // value is 10€ in minor units
        paymentRequest.setAmount(amount);

        var orderRef = UUID.randomUUID().toString();
        paymentRequest.setReference(orderRef); // required
        // required for 3ds2 redirect flow
        paymentRequest.setReturnUrl("http://localhost:8080/api/handleShopperRedirect?orderRef=" + orderRef);

        // required for 3ds2 native flow
        paymentRequest.setAdditionalData(Collections.singletonMap("allow3DS2", "true"));
        // required for 3ds2 native flow
        paymentRequest.setOrigin("http://localhost:8080");
        // required for 3ds2
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        // required by some issuers for 3ds2
        paymentRequest.setShopperIP(request.getRemoteAddr());

        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        // required for Klarna
        if (body.getPaymentMethod().getType().contains("klarna")) {
            paymentRequest.setCountryCode("DE");
            paymentRequest.setShopperReference("1234");
            paymentRequest.setShopperEmail("youremail@email.com");
            paymentRequest.setShopperLocale("en_US");
            var lineItems = new ArrayList<LineItem>();
            lineItems.add(
                new LineItem().quantity(1L).amountExcludingTax(331L).taxPercentage(2100L).description("Sunglasses").id("Item 1").taxAmount(69L).amountIncludingTax(400L)
            );
            lineItems.add(
                new LineItem().quantity(2L).amountExcludingTax(248L).taxPercentage(2100L).description("Headphones").id("Item 2").taxAmount(52L).amountIncludingTax(300L)
            );
            paymentRequest.setLineItems(lineItems);
        }

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
    public ResponseEntity<PaymentsDetailsResponse> payments(@RequestBody PaymentsDetailsRequest detailsRequest) throws IOException, ApiException {
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
        var detailsRequest = new PaymentsDetailsRequest();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            detailsRequest.setDetails(Collections.singletonMap("redirectResult", redirectResult));
        } else if (payload != null && !payload.isEmpty()) {
            detailsRequest.setDetails(Collections.singletonMap("payload", payload));
        }

        return getRedirectView(detailsRequest);
    }

    private RedirectView getRedirectView(final PaymentsDetailsRequest detailsRequest) throws ApiException, IOException {
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

    /* ################# UTILS ###################### */
    private String findCurrency(String type) {
        switch (type) {
            case "ach":
                return "USD";
            case "wechatpayqr":
            case "alipay":
                return "CNY";
            case "dotpay":
                return "PLN";
            case "boletobancario":
            case "boletobancario_santander":
                return "BRL";
            default:
                return "EUR";
        }
    }
    /* ################# end UTILS ###################### */
}
