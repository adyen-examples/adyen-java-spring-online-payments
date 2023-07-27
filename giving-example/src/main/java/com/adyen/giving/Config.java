package com.adyen.giving;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;


public class Config {
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
