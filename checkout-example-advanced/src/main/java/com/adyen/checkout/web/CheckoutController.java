package com.adyen.checkout.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {
    @Value("${ADYEN_CLIENT_KEY}")
    private String clientKey;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/preview")
    public String preview(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        return "preview";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("clientKey", clientKey);
        return "checkout";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "result";
    }
}
