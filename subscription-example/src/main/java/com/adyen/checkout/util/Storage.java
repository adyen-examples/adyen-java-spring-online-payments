package com.adyen.checkout.util;

import java.util.HashSet;
import java.util.Set;

public class Storage {

    public static final String SHOPPER_REFERENCE = "YOUR_UNIQUE_SHOPPER_ID_IOfW3k9G2PvYuJiol";

   private record Token(String recurringReference, String paymentMethod, String shopperReference) {
   }

   private static Set<Token> tokens = new HashSet<>();

   public static Set<Token> getAllTokens() {
       return tokens;
   }

   public static void add(String token, String paymentMethod, String shopperReference) {
       tokens.add(new Token(token, paymentMethod, shopperReference));
   }

   public static void remove(String token, String paymentMethod, String shopperReference) {
        tokens.remove(new Token(token, paymentMethod, shopperReference));
   }
}
