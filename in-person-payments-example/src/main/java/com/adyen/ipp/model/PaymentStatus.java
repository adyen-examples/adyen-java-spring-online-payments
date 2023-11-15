package com.adyen.ipp.model;

public enum PaymentStatus {
    /**
     * Indicates that the customer has not paid yet.
     */
    NotPaid,

    /**
     * Indicates that the customer is going to pay, e.g. the payment request is sent to the terminal.
     */
    PaymentInProgress,

    /**
     * Indicates that the customer has paid for the table, e.g. successful payment request.
     */
    Paid,

    /**
     * A refund is set to {@link #RefundInProgress} when the merchant has initiated the referenced refund process.
     * Referenced refunds are processed asynchronously and are updated through webhooks.
     * See also <a href="https://docs.adyen.com/point-of-sale/basic-tapi-integration/refund-payment/referenced/">Referenced Refunds</a>.
     */
    RefundInProgress,

    /**
     * A refund is set to {@link #Refunded} when the webhook CANCEL_OR_REFUND is successfully received.
     * See <a href="https://docs.adyen.com/point-of-sale/basic-tapi-integration/refund-payment/refund-webhooks/#cancel-or-refund-webhook">Cancel or Refund Webhook</a>.
     */
    Refunded,

    /**
     * A refund is set to {@link #RefundFailed} when the webhook REFUND_FAILED is successfully received.
     * See <a href="https://docs.adyen.com/online-payments/refund#refund-failed">Refund Failed</a>.
     */
    RefundFailed,

    /**
     * A refund is set to {@link #RefundedReversed} when the webhook REFUNDED_REVERSED is successfully received.
     * See <a href="https://docs.adyen.com/online-payments/refund/#refunded-reversed">Refunded Reversed</a>.
     */
    RefundedReversed
}