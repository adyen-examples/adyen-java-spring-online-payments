package com.adyen.paybylink.model;

public class NewLinkRequest {

    private Long amount;
    private String reference;

    public NewLinkRequest() {
    }

    public NewLinkRequest(Long amount, String reference) {
        this.amount = amount;
        this.reference = reference;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
