package com.adyen.paybylink.service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.Amount;
import com.adyen.model.checkout.PaymentLinkRequest;
import com.adyen.model.checkout.PaymentLinkResponse;
import com.adyen.paybylink.ApplicationProperty;
import com.adyen.paybylink.model.NewLinkRequest;
import com.adyen.service.checkout.PaymentLinksApi;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentLinkService {
    private final Logger log = LoggerFactory.getLogger(PaymentLinkService.class);

    private static final HashMap<String, PaymentLinkResponse> links = new HashMap<>();

    private final ApplicationProperty applicationProperty;

    private final PaymentLinksApi paymentLinksApi;

    public PaymentLinkService(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentLinksApi = new PaymentLinksApi(client);
    }

    public List<PaymentLinkResponse> getLinks(){
        updateLinks();
        return new ArrayList<>(links.values());
    }

    public PaymentLinkResponse getLink(String id){
        updateLink(id);
        return links.get(id);
    }

    public PaymentLinkResponse addLink(NewLinkRequest request, String returnUrl) throws IOException, ApiException {

        PaymentLinkResponse paymentLinkResource = createPaymentLink(request.getAmount(), request.getReference(), returnUrl);
        links.put(paymentLinkResource.getId(), paymentLinkResource);

        return paymentLinkResource;
    }

    /**
     * Update routine to get the latest status of all links
     */

    private void updateLinks(){
        links.forEach((key, value) -> updateLink(key));
    }

    public void updateLink(String id) {
        try {
            links.put(id, getPaymentLink(id));
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adyen API Wrapper
     */
    private PaymentLinkResponse getPaymentLink(String id) throws IOException, ApiException {
        return paymentLinksApi.getPaymentLink(id);
    }

    private PaymentLinkResponse createPaymentLink(Long value, String reference, String returnUrl) throws IOException, ApiException {

        Amount amount = new Amount();
        amount.currency("EUR");
        amount.value(value * 100);  // convert to minor units

        if(reference == null) {
            reference = UUID.randomUUID().toString();
        }

        PaymentLinkRequest createPaymentLinkRequest = new PaymentLinkRequest();
        createPaymentLinkRequest.merchantAccount(applicationProperty.getMerchantAccount());
        createPaymentLinkRequest.setReturnUrl(returnUrl);
        createPaymentLinkRequest.amount(amount);
        createPaymentLinkRequest.reference(reference);

        return paymentLinksApi.paymentLinks(createPaymentLinkRequest);
    }
}
