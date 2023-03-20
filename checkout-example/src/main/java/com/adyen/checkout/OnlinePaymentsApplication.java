package com.adyen.checkout;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class OnlinePaymentsApplication {

    private static final Logger log = LoggerFactory.getLogger(OnlinePaymentsApplication.class);

    @Autowired
    private ApplicationProperty applicationProperty;

    public static void main(String[] args) {
        SpringApplication.run(OnlinePaymentsApplication.class, args);
    }

    @PostConstruct
    public void init() {
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running on http://localhost:" + applicationProperty.getServerPort() +
            "\n----------------------------------------------------------");
    }

}
