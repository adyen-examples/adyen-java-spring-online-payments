package com.adyen.checkout.views;

import com.adyen.checkout.ApplicationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    @Autowired
    private ApplicationProperty applicationProperty;

    @Autowired
    public ViewController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getClientKey() == null) {
            Logger log = LoggerFactory.getLogger(ViewController.class);
            log.warn("ADYEN_CLIENT_KEY is undefined ");
        }
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/preview")
    public String preview(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        return "preview";
    }

    @GetMapping("/checkout/dropin")
    public String checkoutDropin(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "checkout/dropin";
    }

    @GetMapping("/checkout/card")
    public String checkoutCard(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "checkout/card";
    }

    @GetMapping("/checkout/googlepay")
    public String checkoutGooglepay(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "checkout/googlepay";
    }

    @GetMapping("/checkout/ideal")
    public String checkoutiDeal(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "checkout/ideal";
    }

    @GetMapping("/checkout/sepa")
    public String checkoutSepa(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "checkout/sepa";
    }

    @GetMapping("/checkout/klarna")
    public String checkoutKlarna(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "checkout/klarna";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "result";
    }
}