package com.adyen.paybylink.api;

import com.adyen.model.checkout.PaymentLinkResponse;
import com.adyen.paybylink.ApplicationProperty;
import com.adyen.paybylink.model.NewLinkRequest;
import com.adyen.paybylink.service.PaymentLinkService;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentLinkController {

    private final Logger log = LoggerFactory.getLogger(PaymentLinkController.class);

    @Autowired
    private PaymentLinkService paymentLinkService;


    public PaymentLinkController(ApplicationProperty applicationProperty) {

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        this.paymentLinkService = new PaymentLinkService(applicationProperty);
    }

    @PostMapping("/links")
    public PaymentLinkResponse createLink(@RequestBody NewLinkRequest newLinkRequest, HttpServletRequest request) throws IOException, ApiException {

        String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return paymentLinkService.addLink(newLinkRequest, returnUrl);
    }

    @GetMapping("/links")
    public List<PaymentLinkResponse> getAllLinks(){
        return paymentLinkService.getLinks();
    }

    @GetMapping("/links/{id}")
    public PaymentLinkResponse getLink(@PathVariable String id){
        return paymentLinkService.getLink(id);
    }
}
