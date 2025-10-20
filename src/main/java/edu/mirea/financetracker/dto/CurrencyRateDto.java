package edu.mirea.financetracker.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CurrencyRateDto {
    private String base_code; // Например: "RUB"
    private Map<String, Double> rates; // Курсы: "USD" -> 0.0113 и т.д.
}