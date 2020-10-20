package com.adyen.checkout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlinePaymentsApplication {
    private static final Logger log = LoggerFactory.getLogger(OnlinePaymentsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OnlinePaymentsApplication.class, args);
        log.info("\n----------------------------------------------------------\n\t" +
            "Application is running! Access URLs:\n\t" +
            "Local: \t\thttp://localhost:8080\n\t" +
            "\n----------------------------------------------------------");
    }

}
