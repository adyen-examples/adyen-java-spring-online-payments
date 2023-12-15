package com.adyen.giving.service;

import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DonationService {
    @Autowired
    protected HttpSession session;

    private static final String DONATION_TOKEN = "DonationToken";

    private static final String PAYMENT_ORIGINAL_PSPREFERENCE = "PaymentOriginalPspReference";

    public void setDonationTokenAndOriginalPspReference(String donationToken, String originalPspReference) throws NullPointerException {
        if (donationToken == null) {
            throw new NullPointerException("No donationToken is found. The payments endpoint did not return a donationToken, please enable this in your Customer Area. See README.");
        }

        session.setAttribute(PAYMENT_ORIGINAL_PSPREFERENCE, originalPspReference);
        session.setAttribute(DONATION_TOKEN, donationToken);
    }

    public String getDonationToken() throws NotFoundException {
        var donationToken = session.getAttribute(DONATION_TOKEN);
        if (donationToken == null) {
            throw new NotFoundException("Could not find donationToken in the sessions");
        }
        return (String) donationToken;
    }

    public String getPaymentOriginalPspReference() throws NotFoundException {
        var pspReference = session.getAttribute(PAYMENT_ORIGINAL_PSPREFERENCE);
        if (pspReference == null) {
            throw new NotFoundException("Could not find originalPspReference in the sessions");
        }
        return (String) pspReference;
    }
}