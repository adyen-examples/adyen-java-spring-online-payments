package com.adyen.giving.util;

import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.NotFoundException;

public final class DonationUtil {
    private static final String DONATION_TOKEN = "DonationToken";

    private static final String PAYMENT_ORIGINAL_PSPREFERENCE = "PaymentOriginalPspReference";

    public static void setDonationTokenAndOriginalPspReference(HttpSession session, String donationToken, String originalPspReference) throws NullPointerException {
        if (donationToken == null) {
            throw new NullPointerException("No donationToken is found. The payments endpoint did not return a donationToken, please enable this in your Customer Area. See README.");
        }

        session.setAttribute(PAYMENT_ORIGINAL_PSPREFERENCE, originalPspReference);
        session.setAttribute(DONATION_TOKEN, donationToken);
    }

    public static String getDonationToken(HttpSession session) throws NotFoundException {
        var donationToken = session.getAttribute(DONATION_TOKEN);
        if (donationToken == null) {
            throw new NotFoundException("Could not find donationToken in the sessions");
        }
        return (String) donationToken;
    }

    public static String getPaymentOriginalPspReference(HttpSession session) throws NotFoundException {
        var pspReference = session.getAttribute(PAYMENT_ORIGINAL_PSPREFERENCE);
        if (pspReference == null) {
            throw new NotFoundException("Could not find originalPspReference in the sessions");
        }
        return (String) pspReference;
    }
}