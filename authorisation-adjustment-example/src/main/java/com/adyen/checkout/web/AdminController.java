package com.adyen.checkout.web;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.model.PaymentModel;
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
@RequestMapping("/admin")
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

    @GetMapping("/")
    public String adminIndex(Model model) {
        model.addAttribute("data", Storage.getAll());
        return "admin/index";
    }

    @GetMapping("/result/{status}/{reference}")
    public String adminResult(@PathVariable String status,
                              @PathVariable String reference,
                              @RequestParam(required = false) String refusalReason,
                              Model model) {
        String result;

        if ("received".equals(status)) {
            result = "success";
        } else {
            result = "error";
        }

        model.addAttribute("title", "Adyen Admin Result");
        model.addAttribute("type", result);
        model.addAttribute("reference", reference);
        model.addAttribute("refusalReason", refusalReason);

        return "admin/result";
    }

    @GetMapping("/admin/details/{reference}")
    public String adminDetails(@PathVariable String reference, Model model) {
        // Assuming getByMerchantReference is a method to fetch data by reference
        // You should implement this method to retrieve data based on the reference
        PaymentModel data = Storage.findByMerchantReference(reference);

        model.addAttribute("title", "Adyen Admin Payment History");
        model.addAttribute("data", data);

        return "admin/details"; // Assuming you have a template named "admin/details.html"
    }

    @PostMapping("/admin/capture-payment")
    public ResponseEntity<PaymentCaptureResponse> capturePayment(@RequestBody String reference) {
        try {
            PaymentModel payment = Storage.findByMerchantReference(reference);

            if (payment == null) {
                throw new Exception("Payment not found in storage - Reference: " + reference);
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
    public ResponseEntity<PaymentAmountUpdateResponse> updatePaymentAmount(@RequestBody String reference) {
        try {
            PaymentModel payment = Storage.findByMerchantReference(reference);

            if (payment == null) {
                throw new Exception("Payment not found in storage - Reference: " + reference);
            }

            var paymentAmountUpdateRequest = new PaymentAmountUpdateRequest();
            paymentAmountUpdateRequest.setMerchantAccount(applicationProperty.getMerchantAccount());
            paymentAmountUpdateRequest.setReference(payment.getMerchantReference());
            paymentAmountUpdateRequest.setIndustryUsage(PaymentAmountUpdateRequest.IndustryUsageEnum.DELAYEDCHARGE);

            var amount = new Amount();
            amount.setValue(payment.getAmount());
            amount.setCurrency(payment.getCurrency());
            paymentAmountUpdateRequest.setAmount(amount);

            var response = modificationsApi.updateAuthorisedAmount(payment.getPspReference(), paymentAmountUpdateRequest);

            log.info(response.toJson());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }


    @PostMapping("/admin/reversal-payment")
    public ResponseEntity<PaymentReversalResponse> reversalPayment(@RequestBody String reference) {
        try {
            PaymentModel payment = Storage.findByMerchantReference(reference);

            if (payment == null) {
                throw new Exception("Payment not found in storage - Reference: " + reference);
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
