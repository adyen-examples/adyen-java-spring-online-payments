package com.adyen.checkout;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

@SpringBootTest
class OnlinePaymentsApplicationTests {

    @BeforeAll
    public static void onceExecutedBeforeAll() {
        System.setProperty("ADYEN_API_KEY", "testKey");
        System.setProperty("ADYEN_MERCHANT_ACCOUNT", "testAccount");
        System.setProperty("ADYEN_CLIENT_KEY", "testKey");
    }

    @AfterAll
    public static void onceExecutedAfterAll(){
        System.clearProperty("ADYEN_API_KEY");
        System.clearProperty("ADYEN_MERCHANT_ACCOUNT");
        System.clearProperty("ADYEN_CLIENT_KEY");
    }

}
