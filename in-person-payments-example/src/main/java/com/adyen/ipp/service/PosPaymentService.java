package com.adyen.ipp.service;

import com.adyen.model.nexo.*;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.UUID;

@Service
public class PosPaymentService {
    @Autowired
    private TerminalCloudApiService terminalCloudAPIService;

    private DatatypeFactory dataTypeFactory;

    public PosPaymentService() throws DatatypeConfigurationException {
        dataTypeFactory = DatatypeFactory.newInstance();
    }

    public TerminalAPIResponse sendPaymentRequest(String serviceId, String poiId, String saleId, String currency, BigDecimal amount) throws IOException, ApiException {
        TerminalAPIRequest request = getPaymentRequest(serviceId, poiId, saleId, currency, amount);
        return terminalCloudAPIService.getTerminalCloudApi().sync(request);
    }

    private TerminalAPIRequest getPaymentRequest(String serviceId, String poiId, String saleId, String currency, BigDecimal amount) {
        var messageHeader = new MessageHeader();
        messageHeader.setMessageCategory(MessageCategoryType.PAYMENT);
        messageHeader.setMessageClass(MessageClassType.SERVICE);
        messageHeader.setMessageType(MessageType.REQUEST);
        messageHeader.setPOIID(poiId);
        messageHeader.setSaleID(saleId);
        messageHeader.setServiceID(serviceId);

        var saleTransactionIdentification = new TransactionIdentification();
        saleTransactionIdentification.setTransactionID(UUID.randomUUID().toString());
        saleTransactionIdentification.setTimeStamp(dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));

        var saleData = new SaleData();
        saleData.setSaleTransactionID(saleTransactionIdentification);

        var amountsReq = new AmountsReq();
        amountsReq.setCurrency(currency);
        amountsReq.setRequestedAmount(amount);

        var paymentTransaction = new PaymentTransaction();
        paymentTransaction.setAmountsReq(amountsReq);

        var paymentRequest = new PaymentRequest();
        paymentRequest.setSaleData(saleData);
        paymentRequest.setPaymentTransaction(paymentTransaction);

        var saleToPOIRequest = new SaleToPOIRequest();
        saleToPOIRequest.setMessageHeader(messageHeader);
        saleToPOIRequest.setPaymentRequest(paymentRequest);

        var terminalAPIRequest = new TerminalAPIRequest();
        terminalAPIRequest.setSaleToPOIRequest(saleToPOIRequest);

        return terminalAPIRequest;
    }
}