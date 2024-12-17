package com.adyen.checkout.configurations;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.util.HMACValidator;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfiguration {
    private final Logger log = LoggerFactory.getLogger(DependencyInjectionConfiguration.class);
    private final ApplicationConfiguration applicationConfiguration;

    public DependencyInjectionConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;

        // https://docs.adyen.com/development-resources/api-credentials/#generate-api-key
        if (applicationConfiguration.getAdyenApiKey() == null) {
            log.error("ADYEN_API_KEY is null");
            //throw new RuntimeException("ADYEN_API_KEY is null");
        }

        // https://docs.adyen.com/account/account-structure/#merchant-accounts
        if (applicationConfiguration.getAdyenMerchantAccount() == null) {
            log.error("ADYEN_MERCHANT_ACCOUNT is null");
            //throw new RuntimeException("ADYEN_MERCHANT_ACCOUNT is null");
        }

        // https://docs.adyen.com/development-resources/client-side-authentication/#get-your-client-key
        if (applicationConfiguration.getAdyenClientKey() == null) {
            log.error("ADYEN_CLIENT_KEY is null");
            //throw new RuntimeException("ADYEN_CLIENT_KEY is null");
        }
    }

    @Bean
    Client client() {
        Config config = new Config();
        config.setApiKey(applicationConfiguration.getAdyenApiKey());
        config.setEnvironment(Environment.TEST);
        return new Client(config);
    }

    @Bean
    PaymentsApi paymentsApi() {
        return new PaymentsApi(client());
    }

    @Bean
    HMACValidator hmacValidator() {
        return new HMACValidator();
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
