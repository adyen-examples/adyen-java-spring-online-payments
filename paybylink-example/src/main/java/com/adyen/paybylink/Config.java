package com.adyen.paybylink;

import com.adyen.util.HMACValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
