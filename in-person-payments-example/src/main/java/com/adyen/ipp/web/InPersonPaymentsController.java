package com.adyen.ipp.web;

import com.adyen.ipp.ApplicationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InPersonPaymentsController {

    private final Logger log = LoggerFactory.getLogger(InPersonPaymentsController.class);

    @Autowired
    public SubscriptionController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(this.applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_API_KEY is undefined ");
        }
    }

    @Autowired
    private ApplicationProperty applicationProperty;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/preview")
    public String preview(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        return "preview";
    }

    @GetMapping("/subscription")
    public String subscription(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "subscription";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "result";
    }

    @GetMapping("/redirect")
    public String redirect(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "redirect";
    }
}
