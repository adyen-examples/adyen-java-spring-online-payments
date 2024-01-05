package com.adyen.checkout.web;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.model.PaymentModel;
import com.adyen.checkout.request.CapturePaymentRequest;
import com.adyen.checkout.request.ReversalPaymentRequest;
import com.adyen.checkout.request.UpdatePaymentAmountRequest;
import com.adyen.checkout.util.Storage;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.ModificationsApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AdminController {
    private final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ModificationsApi modificationsApi;

    @Autowired
    private ApplicationProperty applicationProperty;

    @Autowired
    public AdminController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }
        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.modificationsApi = new ModificationsApi(client);
    }

    @GetMapping("/admin")
    public String index(Model model) {
        model.addAttribute("data", Storage.getAll());
        return "admin/index";
    }

    @GetMapping("/admin/result/{status}/{reference}")
    public String result(@PathVariable String status, @PathVariable String reference, @RequestParam(required = false) String refusalReason, Model model) {
        String result;

        if (status.equals("received")) {
            result = "success";
        } else {
            result = "error";
        }

        model.addAttribute("type", result);
        model.addAttribute("reference", reference);
        model.addAttribute("refusalReason", refusalReason);

        return "admin/result";
    }

    @GetMapping("/admin/details/{reference}")
    public String details(@PathVariable String reference, Model model) {
        PaymentModel data = Storage.findByMerchantReference(reference);
        model.addAttribute("data", data);
        return "admin/details";
    }

    @PostMapping("/admin/capture-payment")
    public ResponseEntity<PaymentCaptureResponse> capturePayment(@RequestBody CapturePaymentRequest request) {
        try {
            PaymentModel payment = Storage.findByMerchantReference(request.getReference());

            if (payment == null) {
                throw new Exception("Payment not found in storage - Reference: " + request.getReference());
            }

            var paymentCaptureRequest = new PaymentCaptureRequest();
            paymentCaptureRequest.setMerchantAccount(applicationProperty.getMerchantAccount());
            paymentCaptureRequest.setReference(payment.getMerchantReference());

            var amount = new Amount();
            amount.setValue(payment.getAmount());
            amount.setCurrency(payment.getCurrency());
            paymentCaptureRequest.setAmount(amount);

            var response = modificationsApi.captureAuthorisedPayment(payment.getPspReference(), paymentCaptureRequest);
            log.info(response.toJson());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/admin/update-payment-amount")
    public ResponseEntity<PaymentAmountUpdateResponse> updatePaymentAmount(@RequestBody UpdatePaymentAmountRequest request) {
        try {
            PaymentModel payment = Storage.findByMerchantReference(request.getReference());

            if (payment == null) {
                throw new Exception("Payment not found in storage - Reference: " + request.getReference());
            }

            var paymentAmountUpdateRequest = new PaymentAmountUpdateRequest();
            paymentAmountUpdateRequest.setMerchantAccount(applicationProperty.getMerchantAccount());
            paymentAmountUpdateRequest.setReference(payment.getMerchantReference());
            paymentAmountUpdateRequest.setIndustryUsage(PaymentAmountUpdateRequest.IndustryUsageEnum.DELAYEDCHARGE);

            var a = new Amount();
            a.setValue(request.getAmount());
            a.setCurrency(payment.getCurrency());
            paymentAmountUpdateRequest.setAmount(a);

            var response = modificationsApi.updateAuthorisedAmount(payment.getPspReference(), paymentAmountUpdateRequest);
            log.info(response.toJson());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/admin/reversal-payment")
    public ResponseEntity<PaymentReversalResponse> reversalPayment(@RequestBody ReversalPaymentRequest request) {
        try {
            PaymentModel payment = Storage.findByMerchantReference(request.getReference());

            if (payment == null) {
                throw new Exception("Payment not found in storage - Reference: " + request.getReference());
            }

            var paymentReversalRequest = new PaymentReversalRequest();
            paymentReversalRequest.setMerchantAccount(applicationProperty.getMerchantAccount());
            paymentReversalRequest.setReference(payment.getMerchantReference());

            var response = modificationsApi.refundOrCancelPayment(payment.getPspReference(), paymentReversalRequest);
            log.info(response.toJson());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
