package com.adyen.ipp.service;

import com.adyen.ipp.util.IdUtility;
import com.adyen.model.nexo.*;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PosTransactionStatusService {
    @Autowired
    private TerminalCloudApiService terminalCloudAPIService;

    public TerminalAPIResponse sendTransactionStatusRequest(String serviceId, String poiId, String saleId) throws IOException, ApiException {
        TerminalAPIRequest request = getTransactionStatusRequest(serviceId, poiId, saleId);
        return terminalCloudAPIService.getTerminalCloudApi().sync(request);
    }

    private TerminalAPIRequest getTransactionStatusRequest(String serviceId, String poiId, String saleId) {
        var messageHeader = new MessageHeader();
        messageHeader.setMessageCategory(MessageCategoryType.TRANSACTION_STATUS);
        messageHeader.setMessageClass(MessageClassType.SERVICE);
        messageHeader.setMessageType(MessageType.REQUEST);
        messageHeader.setPOIID(poiId);
        messageHeader.setSaleID(saleId);
        messageHeader.setServiceID(IdUtility.getRandomAlphanumericId(10));

        var messageReference = new MessageReference();
        messageReference.setMessageCategory(MessageCategoryType.PAYMENT);
        messageReference.setServiceID(serviceId);
        messageReference.setSaleID(saleId);

        var transactionStatusRequest = new TransactionStatusRequest();
        transactionStatusRequest.setMessageReference(messageReference);
        transactionStatusRequest.setReceiptReprintFlag(true);
        transactionStatusRequest.getDocumentQualifier().add(DocumentQualifierType.CASHIER_RECEIPT);
        transactionStatusRequest.getDocumentQualifier().add(DocumentQualifierType.CUSTOMER_RECEIPT);

        var saleToPOIRequest = new SaleToPOIRequest();
        saleToPOIRequest.setMessageHeader(messageHeader);
        saleToPOIRequest.setTransactionStatusRequest(transactionStatusRequest);

        var terminalAPIRequest = new TerminalAPIRequest();
        terminalAPIRequest.setSaleToPOIRequest(saleToPOIRequest);

        return terminalAPIRequest;
    }
}