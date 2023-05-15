package com.adyen.checkout.web;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.util.Storage;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.model.recurring.DisableRequest;
import com.adyen.service.RecurringApi;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final PaymentsApi paymentsApi;
    private final RecurringApi recurring;

    @Autowired
    public AdminController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
        this.recurring = new RecurringApi(client);

    }

    @Autowired
    private ApplicationProperty applicationProperty;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tokens", Storage.getAllTokens());
        return "admin/index";
    }

    @GetMapping("/makepayment/{recurringDetailReference}")
    public String payment(@PathVariable String recurringDetailReference, Model model) {
        log.info("/admin/makepayment/{}", recurringDetailReference);

        String result;
        PaymentResponse response = null;

        try {
            var orderRef = UUID.randomUUID().toString();

            var paymentRequest = new PaymentRequest();
            paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
            paymentRequest.setAmount(new Amount().currency("EUR").value(1199L));
            paymentRequest.setReference(orderRef);
            paymentRequest.setShopperInteraction(PaymentRequest.ShopperInteractionEnum.CONTAUTH);
            paymentRequest.setShopperReference(Storage.SHOPPER_REFERENCE);
            paymentRequest.setRecurringProcessingModel(PaymentRequest.RecurringProcessingModelEnum.SUBSCRIPTION);
            paymentRequest.setPaymentMethod(new CheckoutPaymentMethod(new StoredPaymentMethodDetails().storedPaymentMethodId(recurringDetailReference)));


            response = this.paymentsApi.payments(paymentRequest);
            log.info("payment response {}", response);

            if(response.getResultCode().equals(PaymentResponse.ResultCodeEnum.AUTHORISED)) {
                result = "success";
            } else {
                result = "error";
            }

        } catch (ApiException e) {
            log.error("ApiException", e);
            result = "error";
        } catch (Exception e) {
        log.error("Unexpected error while performing the payment", e);
        result = "error";
        }

        model.addAttribute("result", result);
        model.addAttribute("recurringDetailReference", recurringDetailReference);

        return "admin/makepayment";
    }

    @GetMapping("/disable/{recurringDetailReference}")
    public String disable(@PathVariable String recurringDetailReference, Model model) {
        log.info("/admin/disable/{}", recurringDetailReference);

        String result;

        try {
            var disableRequest = new DisableRequest();
            disableRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
            disableRequest.setShopperReference(Storage.SHOPPER_REFERENCE);
            disableRequest.setRecurringDetailReference(recurringDetailReference);

            var response = this.recurring.disable(disableRequest);
            log.info("disable response {}", response);

            Storage.remove(recurringDetailReference, Storage.SHOPPER_REFERENCE);

            log.info("remove token {}", recurringDetailReference);
            result = "success";

        } catch (Exception e) {
            log.error("Unexpected error while disabling the token", e);
            result = "error";
        }

        model.addAttribute("result", result);
        model.addAttribute("recurringDetailReference", recurringDetailReference);

        return "admin/disable";
    }

}
