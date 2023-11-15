package com.adyen.ipp.web;

import com.adyen.ipp.ApplicationProperty;
import com.adyen.ipp.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class InPersonPaymentsWebController {

    private final Logger log = LoggerFactory.getLogger(InPersonPaymentsWebController.class);

    @Autowired
    private TableService tableService;

    @Autowired
    public InPersonPaymentsWebController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if(this.applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_API_KEY is undefined ");
        }
    }

    @Autowired
    private ApplicationProperty applicationProperty;

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/cashregister")
    public String cashregister(Model model) {
        model.addAttribute("poiId", applicationProperty.getPoiId());
        model.addAttribute("saleId", applicationProperty.getSaleId());
        model.addAttribute("tables", tableService.getTables());
        return "cashregister";
    }

    @GetMapping("/transaction-status/{tableName}")
    public String transactionstatus(@PathVariable String tableName) {
        return "transactionstatus";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "result";
    }
}
