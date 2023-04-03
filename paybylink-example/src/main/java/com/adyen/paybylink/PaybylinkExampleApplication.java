package com.adyen.paybylink;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaybylinkExampleApplication {

    private static final Logger log = LoggerFactory.getLogger(PaybylinkExampleApplication.class);

    @Autowired
    private ApplicationProperty applicationProperty;

    public static void main(String[] args) {
        SpringApplication.run(PaybylinkExampleApplication.class, args);
    }

    @PostConstruct
    public void init() {
        log.info("\n----------------------------------------------------------\n\t" +
            "Application is running on http://localhost:" + applicationProperty.getServerPort() +
            "\n----------------------------------------------------------");
    }

}
