package com.adyen.ipp.util;

import java.security.SecureRandom;

public class IdUtility {
    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Gets a random selection from any {@code ALPHANUMERIC_CHARACTERS} with the specified {@code length}.
     *
     * @param length Length of the generated id.
     * @return Alphanumeric Id.
     */
    public static String getRandomAlphanumericId(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC_CHARACTERS.charAt(RANDOM.nextInt(ALPHANUMERIC_CHARACTERS.length())));
        }

        return sb.toString();
    }
}
