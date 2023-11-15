package com.adyen.ipp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

@SpringBootTest
class InPersonPaymentsApplicationTests {

    @BeforeAll
    public static void onceExecutedBeforeAll() {
        System.setProperty("ADYEN_API_KEY", "testKey");
    }

    @AfterAll
    public static void onceExecutedAfterAll(){
        System.clearProperty("ADYEN_API_KEY");
    }
}
