package com.adyen.checkout.api;

import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.details.AchDetails;
import com.adyen.model.checkout.details.DotpayDetails;
import com.adyen.model.checkout.details.GiropayDetails;
import com.adyen.model.checkout.details.KlarnaDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

@JsonComponent
public class PaymentMethodDetailsDeserializer extends JsonDeserializer<PaymentMethodDetails> {

    @Override
    public PaymentMethodDetails deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        var codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        String type = node.get("type").asText();
        switch (type) {
            case "ach":
                return codec.treeToValue(node, AchDetails.class);
            case "dotpay":
                return codec.treeToValue(node, DotpayDetails.class);
            case "giropay":
                return codec.treeToValue(node, GiropayDetails.class);
            // case "sepadirectdebit":
            //     return codec.treeToValue(node, SepaDirectDebitDetails.class);
            case "klarna":
            case "klarna_paynow":
            case "klarna_account":
                return codec.treeToValue(node, KlarnaDetails.class);
            default:
                return codec.treeToValue(node, DefaultPaymentMethodDetails.class);
        }
    }
}
