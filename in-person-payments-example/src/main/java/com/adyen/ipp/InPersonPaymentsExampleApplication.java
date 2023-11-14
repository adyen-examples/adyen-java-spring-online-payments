package com.adyen.checkout;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class InPersonPaymentsExampleApplication {

    private static final Logger log = LoggerFactory.getLogger(InPersonPaymentsExampleApplication.class);

    @Autowired
    private ApplicationProperty applicationProperty;

    public static void main(String[] args) {
        SpringApplication.run(InPersonPaymentsExampleApplication.class, args);
    }

    @PostConstruct
    public void init() {
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running on http://localhost:" + applicationProperty.getServerPort() +
            "\n----------------------------------------------------------");
    }

}
