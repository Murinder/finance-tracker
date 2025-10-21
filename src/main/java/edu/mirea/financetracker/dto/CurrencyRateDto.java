package edu.mirea.financetracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CurrencyRateDto {
    @JsonProperty("base_code")
    private String baseCode; // Например: "RUB"
    private Map<String, Double> rates; // Курсы: "USD" -> 0.0113 и т.д.
}