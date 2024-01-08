package com.adyen.checkout.util;

import com.adyen.checkout.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
    Local storage for storing pre-authorised payments (pre-authorisations) used in the Admin Panel, payments are saved in-memory.
    Each PaymentModel (pre-authorisation) contains a list of PaymentDetailsModels that gets appended in subsequent actions such as: adjust, extend, capture or reversal.
 */
public class Storage {
    private static List<PaymentModel> payments = new ArrayList<>();

    public static List<PaymentModel> getAll() {
        return payments;
    }

    public static PaymentModel findByMerchantReference(String merchantReference) {
        for (PaymentModel payment : payments) {
            if (payment.getMerchantReference().equals(merchantReference)) {
                return payment;
            }
        }
        return null;
    }

    public static void addPaymentToHistory(PaymentDetailsModel paymentDetailsModel) {
        if (paymentDetailsModel.getMerchantReference() == null) {
            throw new IllegalArgumentException("Merchant Reference is undefined");
        }

        PaymentModel paymentModel = findByMerchantReference(paymentDetailsModel.getMerchantReference());

        if (paymentModel != null) {
            paymentModel.getPaymentDetailsModelList().add(paymentDetailsModel);
        }
    }

    public static void updatePayment(String merchantReference, long amount, LocalDateTime expiryDate) {
        PaymentModel payment = findByMerchantReference(merchantReference);

        if (payment != null) {
            payment.setAmount(amount);
            payment.setExpiryDate(expiryDate);
        }
    }

    public static void put(PaymentModel paymentModel) {
        payments.add(paymentModel);
    }
}
