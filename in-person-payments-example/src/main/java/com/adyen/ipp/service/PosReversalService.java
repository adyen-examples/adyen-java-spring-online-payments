package com.adyen.ipp.service;

import com.adyen.ipp.util.IdUtility;
import com.adyen.model.nexo.*;
import com.adyen.model.terminal.TerminalAPIRequest;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.adyen.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.util.GregorianCalendar;

@Service
public class PosReversalService {
    @Autowired
    private TerminalCloudApiService terminalCloudAPIService;

    private DatatypeFactory dataTypeFactory;

    public PosReversalService() throws DatatypeConfigurationException {
        dataTypeFactory = DatatypeFactory.newInstance();
    }

    public TerminalAPIResponse sendReversalRequest(ReversalReasonType reversalReasonType, String saleTransactionId, String poiTransactionId, String poiId, String saleId) throws IOException, ApiException {
        TerminalAPIRequest request = getReversalRequest(reversalReasonType, saleTransactionId, poiTransactionId, poiId, saleId);
        return terminalCloudAPIService.getTerminalCloudApi().sync(request);
    }

    private TerminalAPIRequest getReversalRequest(ReversalReasonType reversalReasonType, String saleTransactionId, String poiTransactionId, String poiId, String saleId) {
        var messageHeader = new MessageHeader();
        messageHeader.setMessageCategory(MessageCategoryType.REVERSAL);
        messageHeader.setMessageClass(MessageClassType.SERVICE);
        messageHeader.setMessageType(MessageType.REQUEST);
        messageHeader.setPOIID(poiId);
        messageHeader.setSaleID(saleId);
        messageHeader.setServiceID(IdUtility.getRandomAlphanumericId(10));

        var poiTransactionIdentification = new TransactionIdentification();
        poiTransactionIdentification.setTransactionID(poiTransactionId);
        poiTransactionIdentification.setTimeStamp(dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));

        var originalPOITransaction = new OriginalPOITransaction();
        originalPOITransaction.setPOIID(poiId);
        originalPOITransaction.setSaleID(saleId);
        originalPOITransaction.setPOITransactionID(poiTransactionIdentification);

        var saleTransactionIdentification = new TransactionIdentification();
        saleTransactionIdentification.setTransactionID(saleTransactionId);
        saleTransactionIdentification.setTimeStamp(dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));

        var saleData = new SaleData();
        saleData.setSaleTransactionID(saleTransactionIdentification);

        var reversalRequest = new ReversalRequest();
        reversalRequest.setReversalReason(reversalReasonType);
        reversalRequest.setOriginalPOITransaction(originalPOITransaction);
        reversalRequest.setSaleData(saleData);

        var saleToPOIRequest = new SaleToPOIRequest();
        saleToPOIRequest.setMessageHeader(messageHeader);
        saleToPOIRequest.setReversalRequest(reversalRequest);

        var terminalAPIRequest = new TerminalAPIRequest();
        terminalAPIRequest.setSaleToPOIRequest(saleToPOIRequest);

        return terminalAPIRequest;
    }
}