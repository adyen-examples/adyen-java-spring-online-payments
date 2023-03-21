package com.adyen.checkout;

import com.adyen.checkout.zeroconfig.ZeroConfig;
import com.adyen.service.exception.ApiException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

import java.io.IOException;


@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
public class OnlinePaymentsApplication {

    private static final Logger log = LoggerFactory.getLogger(OnlinePaymentsApplication.class);

    @Autowired
    private ApplicationProperty applicationProperty;
    @Autowired
    private ZeroConfig zeroConfig;

    public static void main(String[] args) {
        SpringApplication.run(OnlinePaymentsApplication.class, args);
    }


    @PostConstruct
    public void init() throws IOException, ApiException {
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running on http://localhost:" + applicationProperty.getServerPort() +
            "\n----------------------------------------------------------");

        zeroConfig.init();
    }

}
