package edu.mirea.financetracker.config;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CurrencyValidator {

    // Используем ConcurrentHashMap для потокобезопасности
    private final Set<String> supportedCurrencies = ConcurrentHashMap.newKeySet();

    public void updateSupportedCurrencies(Set<String> currencies) {
        supportedCurrencies.clear();
        supportedCurrencies.addAll(currencies);
    }

    public boolean isValid(String currency) {
        return supportedCurrencies.contains(currency);
    }

    public Set<String> getSupportedCurrencies() {
        return Set.copyOf(supportedCurrencies); // возвращаем неизменяемую копию
    }
}