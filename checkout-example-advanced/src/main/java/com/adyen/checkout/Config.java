package com.adyen.checkout;

import org.springframework.context.annotation.Bean;

import nz.net.ultraq.thymeleaf.LayoutDialect;

public class Config {
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
