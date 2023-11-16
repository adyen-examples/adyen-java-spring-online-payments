package com.adyen.ipp.model;

import java.math.BigDecimal;

public class Table {
    /**
     * Currency used for the Amount (e.g. "EUR", "USD").
     */
    private String currency;

    /**
     * The table amount to-be-paid, in DECIMAL units (example: 42.99), the terminal API does not use minor units.
     */
    private BigDecimal amount;

    /**
     * Name of the table, used to uniquely identify the table.
     */
    private String tableName;

    /**
     * Status of the table, used to check if the table has paid.
     */
    private PaymentStatus paymentStatus = PaymentStatus.NotPaid;

    /**
     * Object that is populated with information when the payment process is started.
     */
    private PaymentStatusDetails paymentStatusDetails;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentStatusDetails getPaymentStatusDetails() {
        return paymentStatusDetails;
    }

    public void setPaymentStatusDetails(PaymentStatusDetails paymentStatusDetails) { this.paymentStatusDetails = paymentStatusDetails; }
}
