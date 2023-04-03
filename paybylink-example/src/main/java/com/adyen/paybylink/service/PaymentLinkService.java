package com.adyen.paybylink.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.CreatePaymentLinkRequest;
import com.adyen.model.checkout.PaymentLinkResource;
import com.adyen.paybylink.ApplicationProperty;
import com.adyen.paybylink.model.NewLinkRequest;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adyen.service.PaymentLinks;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentLinkService {
    private final Logger log = LoggerFactory.getLogger(PaymentLinkService.class);

    private final HashMap<String, PaymentLinkResource> links = new HashMap<>();

    private final ApplicationProperty applicationProperty;

    private final PaymentLinks paymentLinks;

    public PaymentLinkService(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentLinks = new PaymentLinks(client);
    }

    public List<PaymentLinkResource> getLinks(){
        updateLinks();
        return new ArrayList<>(links.values());
    }

    public PaymentLinkResource getLink(String id){
        updateLink(id);
        return links.get(id);
    }

    public PaymentLinkResource addLink(NewLinkRequest request, String returnUrl) throws IOException, ApiException {
        PaymentLinkResource paymentLinkResource = createPaymentLink(request.getAmount(), request.getReference(), returnUrl);
        links.put(paymentLinkResource.getId(), paymentLinkResource);

        return paymentLinkResource;
    }

    /**
     * Update routine to get the latest status of all links
     */

    private void updateLinks(){
        links.forEach((key, value) -> updateLink(key));
    }

    private void updateLink(String id) {
        try {
            links.put(id, getPaymentLink(id));
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adyen API Wrapper
     */

    private PaymentLinkResource getPaymentLink(String id) throws IOException, ApiException {
        return paymentLinks.retrieve(id);
    }

    private PaymentLinkResource createPaymentLink(Long value, String reference, String returnUrl) throws IOException, ApiException {

        Amount amount = new Amount();
        amount.currency("EUR");
        amount.value(value);

        if(reference == null) {
            reference = UUID.randomUUID().toString();
        }

        CreatePaymentLinkRequest createPaymentLinkRequest = new CreatePaymentLinkRequest();
        createPaymentLinkRequest.merchantAccount(applicationProperty.getMerchantAccount());
        createPaymentLinkRequest.setReturnUrl(returnUrl);
        createPaymentLinkRequest.amount(amount);
        createPaymentLinkRequest.reference(reference);

        return paymentLinks.create(createPaymentLinkRequest);
    }
}
