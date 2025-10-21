package edu.mirea.financetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class AppConfig {

    @Bean
    public AtomicReference<String> baseCurrency() {
        return new AtomicReference<>("RUB"); // По умолчанию — рубли
    }
}