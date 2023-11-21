package com.adyen.ipp.api;

import com.adyen.model.nexo.*;
import com.adyen.ipp.ApplicationProperty;
import com.adyen.ipp.model.*;
import com.adyen.ipp.request.*;
import com.adyen.ipp.response.*;
import com.adyen.ipp.service.*;
import com.adyen.ipp.util.IdUtility;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class InPersonPaymentsController {
    private final Logger log = LoggerFactory.getLogger(InPersonPaymentsController.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    private PosAbortService posAbortService;

    @Autowired
    private PosPaymentService posPaymentService;

    @Autowired
    private PosReversalService posReversalService;

    @Autowired
    private PosTransactionStatusService posTransactionStatusService;

    @Autowired
    private TableService tableService;

    @Autowired
    public InPersonPaymentsController(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;
    }

    @PostMapping("/create-payment")
    public ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) throws IOException, ApiException {
        Table table = tableService.getTables().stream()
                .filter(t -> t.getTableName().equals(request.getTableName()))
                .findFirst()
                .orElse(null);

        if (table == null) {
            return ResponseEntity
                    .status(404)
                    .body(new CreatePaymentResponse()
                            .result("failure")
                            .refusalReason("Table " + request.getTableName() + " not found"));
        }

        try {
            String serviceId = IdUtility.getRandomAlphanumericId(10);
            table.getPaymentStatusDetails().setServiceId(serviceId);
            table.setPaymentStatus(PaymentStatus.PaymentInProgress);

            var response = posPaymentService.sendPaymentRequest(serviceId, applicationProperty.getPoiId(), applicationProperty.getSaleId(), request.getCurrency(), request.getAmount());

            if (response == null || response.getSaleToPOIResponse() == null || response.getSaleToPOIResponse().getPaymentResponse() == null) {
                table.setPaymentStatus(PaymentStatus.NotPaid);
                return ResponseEntity
                        .badRequest()
                        .body(new CreatePaymentResponse()
                                .result("failure")
                                .refusalReason("Empty payment response"));
            }

            var paymentResponse = response.getSaleToPOIResponse().getPaymentResponse();

            switch (paymentResponse.getResponse().getResult()) {
                case SUCCESS:
                    table.setPaymentStatus(PaymentStatus.Paid);
                    table.getPaymentStatusDetails().setPoiTransactionId(paymentResponse.getPOIData().getPOITransactionID().getTransactionID());
                    table.getPaymentStatusDetails().setPoiTransactionTimeStamp(paymentResponse.getPOIData().getPOITransactionID().getTimeStamp());
                    table.getPaymentStatusDetails().setSaleTransactionId(paymentResponse.getSaleData().getSaleTransactionID().getTransactionID());
                    table.getPaymentStatusDetails().setSaleTransactionTimeStamp(paymentResponse.getSaleData().getSaleTransactionID().getTimeStamp());

                    return ResponseEntity
                            .ok()
                            .body(new CreatePaymentResponse()
                                    .result("success"));
                case FAILURE:
                    table.setPaymentStatus(PaymentStatus.NotPaid);
                    table.getPaymentStatusDetails().setRefusalReason("Payment terminal responded with: " + paymentResponse.getResponse().getErrorCondition());
                    table.getPaymentStatusDetails().setPoiTransactionId(paymentResponse.getPOIData().getPOITransactionID().getTransactionID());
                    table.getPaymentStatusDetails().setPoiTransactionTimeStamp(paymentResponse.getPOIData().getPOITransactionID().getTimeStamp());
                    table.getPaymentStatusDetails().setSaleTransactionId(paymentResponse.getSaleData().getSaleTransactionID().getTransactionID());
                    table.getPaymentStatusDetails().setSaleTransactionTimeStamp(paymentResponse.getSaleData().getSaleTransactionID().getTimeStamp());

                    return ResponseEntity
                            .ok()
                            .body(new CreatePaymentResponse()
                                    .result("failure")
                                    .refusalReason(table.getPaymentStatusDetails().getRefusalReason()));
                default:
                    table.setPaymentStatus(PaymentStatus.NotPaid);

                    return ResponseEntity
                            .badRequest()
                            .body(new CreatePaymentResponse()
                                    .result("failure")
                                    .refusalReason("Could not reach payment terminal with POI ID " + applicationProperty.getPoiId()));
            }

        } catch (IOException | ApiException e) {
            log.error(e.toString());
            table.setPaymentStatus(PaymentStatus.NotPaid);
            throw e;
        }
    }

    @PostMapping("/create-reversal")
    public ResponseEntity<CreateReversalResponse> createReversal(@RequestBody CreateReversalRequest request) throws IOException, ApiException {
        try {
            Table table = tableService.getTables().stream()
                    .filter(t -> t.getTableName().equals(request.getTableName()))
                    .findFirst()
                    .orElse(null);

            if (table == null) {
                return ResponseEntity
                        .status(404)
                        .body(new CreateReversalResponse()
                                .result("failure")
                                .refusalReason("Table " + request.getTableName() + " not found"));
            }

            var response = posReversalService.sendReversalRequest(ReversalReasonType.MERCHANT_CANCEL, table.getPaymentStatusDetails().getSaleTransactionId(), table.getPaymentStatusDetails().getPoiTransactionId(), applicationProperty.getPoiId(), applicationProperty.getSaleId());

            if (response == null || response.getSaleToPOIResponse() == null || response.getSaleToPOIResponse().getReversalResponse() == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new CreateReversalResponse()
                                .result("failure")
                                .refusalReason("Empty reversal response"));
            }

            var reversalResponse = response.getSaleToPOIResponse().getReversalResponse();

            switch (reversalResponse.getResponse().getResult()) {
                case SUCCESS:
                    table.setPaymentStatus(PaymentStatus.RefundInProgress);
                    return ResponseEntity
                            .ok()
                            .body(new CreateReversalResponse()
                                    .result("success"));
                case FAILURE:
                    table.setPaymentStatus(PaymentStatus.RefundFailed);
                    return ResponseEntity
                            .ok()
                            .body(new CreateReversalResponse()
                                    .result("failure")
                                    .refusalReason("Payment terminal responded with: " + java.net.URLDecoder.decode(reversalResponse.getResponse().getAdditionalResponse())));
                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new CreateReversalResponse()
                                    .result("failure")
                                    .refusalReason("Could not reach payment terminal with POI ID " + applicationProperty.getPoiId()));
            }
        } catch (IOException | ApiException e) {
            log.error(e.toString());
            throw e;
        }
    }

    @GetMapping("/abort/{tableName}")
    public ResponseEntity abort(@PathVariable String tableName) throws IOException, ApiException {
        try {
            Table table = tableService.getTables().stream()
                    .filter(t -> t.getTableName().equals(tableName))
                    .findFirst()
                    .orElse(null);

            if (table == null || table.getPaymentStatusDetails() == null || table.getPaymentStatusDetails().getServiceId() == null) {
                return ResponseEntity.notFound().build();
            }
            var abortResponse = posAbortService.sendAbortRequest(table.getPaymentStatusDetails().getServiceId(), applicationProperty.getPoiId(), applicationProperty.getSaleId());

            return ResponseEntity.ok().body(abortResponse);
        } catch (IOException | ApiException e) {
            log.error(e.toString());
            throw e;
        }
    }
}