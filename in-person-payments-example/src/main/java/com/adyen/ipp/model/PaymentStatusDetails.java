package com.adyen.ipp.model;

import javax.xml.datatype.XMLGregorianCalendar;

public class PaymentStatusDetails {
    /**
     * PspReference: It is possible to get the PspReference from the Response.AdditionalData property:
     * https://docs.adyen.com/point-of-sale/basic-tapi-integration/verify-transaction-status/#id324618081
     */
    private String pspReference;

    /**
     * The API provides a refusal reason when there's an error. This will get populated when "failure" is sent as a response.
     */
    private String refusalReason;

    /**
     * The POI Transaction Id, populated when a TableStatus is set to PaymentStatus.Paid.
     * Example value: CmI6001693237705007.TG6DVRZ3HVTFWR82.
     */
    private String poiTransactionId;

    /**
     * Date of the POI transaction.
     */
    private XMLGregorianCalendar poiTransactionTimeStamp;

    /**
     * The SaleTransactionId (SaleReferenceId), populated when a TableStatus is set to PaymentStatus.Paid.
     * Example value: 6abcb27d-9082-40d9-969d-1c7f283ebd52.
     */
    private String saleTransactionId;

    /**
     * Date of the Sale transaction.
     */
    private XMLGregorianCalendar saleTransactionTimeStamp;

    /**
     * The unique ID of a message pair, which processes the transaction. The value is assigned when you initiate a payment transaction to the terminal, and used to cancel/abort the request.
     * This unique request ID, consisting of 1-10 alphanumeric characters is generated using the Utilities.IdUtility.GetRandomAlphanumericId(int) function and must be unique within the last 48 hours for the terminal (POIID) being used.
     */
    private String serviceId;

    public String getPspReference() {
        return pspReference;
    }

    public void setPspReference(String pspReference) {
        this.pspReference = pspReference;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    public String getPoiTransactionId() {
        return poiTransactionId;
    }

    public void setPoiTransactionId(String poiTransactionId) {
        this.poiTransactionId = poiTransactionId;
    }

    public XMLGregorianCalendar getPoiTransactionTimeStamp() {
        return poiTransactionTimeStamp;
    }

    public void setPoiTransactionTimeStamp(XMLGregorianCalendar poiTransactionTimeStamp) {
        this.poiTransactionTimeStamp = poiTransactionTimeStamp;
    }

    public String getSaleTransactionId() {
        return saleTransactionId;
    }

    public void setSaleTransactionId(String saleTransactionId) {
        this.saleTransactionId = saleTransactionId;
    }

    public XMLGregorianCalendar getSaleTransactionTimeStamp() {
        return saleTransactionTimeStamp;
    }

    public void setSaleTransactionTimeStamp(XMLGregorianCalendar saleTransactionTimeStamp) {
        this.saleTransactionTimeStamp = saleTransactionTimeStamp;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
