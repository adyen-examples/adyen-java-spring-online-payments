package com.adyen.ipp.response;

public class CreatePaymentResponse {
    private String result;
    private String refusalReason;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    public CreatePaymentResponse refusalReason(String refusalReason){
        this.refusalReason = refusalReason;
        return this;
    }
    public CreatePaymentResponse result(String result){
        this.result = result;
        return this;
    }
}
