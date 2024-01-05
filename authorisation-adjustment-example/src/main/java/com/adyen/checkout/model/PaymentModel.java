package com.adyen.checkout.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PaymentModel {
    private String merchantReference;
    private String pspReference;
    private long amount;
    private String currency;
    private LocalDateTime bookingDate;
    private LocalDateTime expiryDate;
    private String paymentMethodBrand;
    private List<PaymentDetailsModel> paymentDetailsModelList;

    public PaymentModel(String merchantReference, String pspReference, long amount, String currency, LocalDateTime bookingDate, LocalDateTime expiryDate, String paymentMethodBrand, List<PaymentDetailsModel> paymentDetailsModelList) {
        this.merchantReference = merchantReference;
        this.pspReference = pspReference;
        this.amount = amount;
        this.currency = currency;
        this.bookingDate = bookingDate;
        this.expiryDate = expiryDate;
        this.paymentMethodBrand = paymentMethodBrand;
        this.paymentDetailsModelList = paymentDetailsModelList;
    }

    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(bookingDate, expiryDate);
    }

    public String getFormattedExpiryDate() {
        return expiryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
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

