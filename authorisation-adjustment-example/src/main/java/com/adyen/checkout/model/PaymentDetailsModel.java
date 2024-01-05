package com.adyen.checkout.model;

import java.time.LocalDateTime;

public class PaymentDetailsModel {
    private String merchantReference;
    private String pspReference;
    private String originalReference;
    private long amount;
    private String currency;
    private LocalDateTime dateTime;
    private String eventCode;
    private String refusalReason;
    private String paymentMethodBrand;
    private boolean success;

    public PaymentDetailsModel(String merchantReference, String pspReference, String originalReference, long amount, String currency, LocalDateTime dateTime, String eventCode, String refusalReason, String paymentMethodBrand, boolean success) {
        this.merchantReference = merchantReference;
        this.pspReference = pspReference;
        this.originalReference = originalReference;
        this.amount = amount;
        this.currency = currency;
        this.dateTime = dateTime;
        this.eventCode = eventCode;
        this.refusalReason = refusalReason;
        this.paymentMethodBrand = paymentMethodBrand;
        this.success = success;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public String getPspReference() {
        return pspReference;
    }

    public void setPspReference(String pspReference) {
        this.pspReference = pspReference;
    }

    public String getOriginalReference() {
        return originalReference;
    }

    public void setOriginalReference(String originalReference) {
        this.originalReference = originalReference;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    public String getPaymentMethodBrand() {
        return paymentMethodBrand;
    }

    public void setPaymentMethodBrand(String paymentMethodBrand) {
        this.paymentMethodBrand = paymentMethodBrand;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
