package com.adyen.ipp.service;

import com.adyen.ipp.util.IdUtility;
import com.adyen.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.adyen.model.nexo.*;
import com.adyen.model.terminal.*;

import java.io.IOException;

@Service
public class PosAbortService {
    @Autowired
    private TerminalCloudApiService terminalCloudAPIService;

    public TerminalAPIResponse sendAbortRequest(String serviceId, String poiId, String saleId) throws IOException, ApiException {
        TerminalAPIRequest request = getAbortRequest(serviceId, poiId, saleId);
        return terminalCloudAPIService.getTerminalCloudApi().sync(request);
    }

    private TerminalAPIRequest getAbortRequest(String serviceId, String poiId, String saleId) {
        var messageHeader = new MessageHeader();
        messageHeader.setMessageCategory(MessageCategoryType.ABORT);
        messageHeader.setMessageClass(MessageClassType.SERVICE);
        messageHeader.setMessageType(MessageType.REQUEST);
        messageHeader.setPOIID(poiId);
        messageHeader.setSaleID(saleId);
        messageHeader.setServiceID(IdUtility.getRandomAlphanumericId(10));

        var messageReference = new MessageReference();
        messageReference.setMessageCategory(MessageCategoryType.PAYMENT);
        messageReference.setServiceID(serviceId);
        messageReference.setPOIID(poiId);
        messageReference.setSaleID(saleId);

        var abortRequest = new AbortRequest();
        abortRequest.setAbortReason("MerchantAbort");
        abortRequest.setMessageReference(messageReference);

        var saleToPOIRequest = new SaleToPOIRequest();
        saleToPOIRequest.setMessageHeader(messageHeader);
        saleToPOIRequest.setAbortRequest(abortRequest);

        var terminalAPIRequest = new TerminalAPIRequest();
        terminalAPIRequest.setSaleToPOIRequest(saleToPOIRequest);

        return terminalAPIRequest;
    }
}