package com.adyen.ipp.web;

import com.adyen.ipp.ApplicationProperty;
import com.adyen.ipp.model.Table;
import com.adyen.ipp.service.PosTransactionStatusService;
import com.adyen.ipp.service.TableService;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
public class InPersonPaymentsWebController {
    private final Logger log = LoggerFactory.getLogger(InPersonPaymentsWebController.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    private TableService tableService;

    @Autowired
    private PosTransactionStatusService posTransactionStatusService;

    @Autowired
    public InPersonPaymentsWebController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/cashregister")
    public String cashregister(Model model) {
        model.addAttribute("poiId", applicationProperty.getPoiId());
        model.addAttribute("saleId", applicationProperty.getSaleId());
        model.addAttribute("tables", tableService.getTables());
        return "cashregister";
    }

    @GetMapping("/transaction-status/{tableName}")
    public String transactionstatus(@PathVariable String tableName, Model model) throws IOException, ApiException {
        Table table = tableService.getTables().stream()
                .filter(t -> t.getTableName().equals(tableName))
                .findFirst()
                .orElse(null);

        if (table == null || table.getPaymentStatusDetails() == null || table.getPaymentStatusDetails().getServiceId() == null) {
            model.addAttribute("errorMessage", "table not found");
            return "transactionstatus";
        }

        var response = posTransactionStatusService.sendTransactionStatusRequest(table.getPaymentStatusDetails().getServiceId(), applicationProperty.getPoiId(), applicationProperty.getSaleId());

        if (response == null ||
                response.getSaleToPOIResponse() == null ||
                response.getSaleToPOIResponse().getTransactionStatusResponse() == null) {
            model.addAttribute("errorMessage", "transaction status response is empty");
            return "transactionstatus";
        }

        var transactionStatusResponse = response.getSaleToPOIResponse().getTransactionStatusResponse();

        if (transactionStatusResponse.getRepeatedMessageResponse() == null ||
                transactionStatusResponse.getRepeatedMessageResponse().getRepeatedResponseMessageBody() == null ||
                transactionStatusResponse.getRepeatedMessageResponse().getRepeatedResponseMessageBody().getPaymentResponse() == null) {
            model.addAttribute("errorMessage", "repeated message response is empty");
            return "transactionstatus";
        }

        var paymentResponse = transactionStatusResponse.getRepeatedMessageResponse().getRepeatedResponseMessageBody().getPaymentResponse();

        model.addAttribute("paymentResponse", paymentResponse);

        return "transactionstatus";
    }

    @GetMapping("/result/{type}/{refusalReason}")
    public String result(@PathVariable String type, @PathVariable(required = false) String refusalReason, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("refusalReason", refusalReason);
        return "result";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "result";
    }
}
