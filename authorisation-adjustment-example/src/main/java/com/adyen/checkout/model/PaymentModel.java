package com.adyen.checkout.model;

import java.util.List;

public class PaymentModel {
    private String merchantReference;
    private String pspReference;
    private long amount;
    private String currency;
    private String bookingDate;
    private String expiryDate;
    private String paymentMethodBrand;
    private List<PaymentDetailsModel> paymentDetailsModelList;

    public PaymentModel(String merchantReference, String pspReference, long amount, String currency, String bookingDate, String expiryDate, String paymentMethodBrand, List<PaymentDetailsModel> paymentDetailsModelList) {
        this.merchantReference = merchantReference;
        this.pspReference = pspReference;
        this.amount = amount;
        this.currency = currency;
        this.bookingDate = bookingDate;
        this.expiryDate = expiryDate;
        this.paymentMethodBrand = paymentMethodBrand;
        this.paymentDetailsModelList = paymentDetailsModelList;
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

    public long getAmount() {
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

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPaymentMethodBrand() {
        return paymentMethodBrand;
    }

    public void setPaymentMethodBrand(String paymentMethodBrand) {
        this.paymentMethodBrand = paymentMethodBrand;
    }

    public List<PaymentDetailsModel> getPaymentDetailsModelList() {
        return paymentDetailsModelList;
    }

    public void setPaymentDetailsModelList(List<PaymentDetailsModel> paymentDetailsModelList) {
        this.paymentDetailsModelList = paymentDetailsModelList;
    }
}

