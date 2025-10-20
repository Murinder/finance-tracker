package edu.mirea.financetracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ForecastDto {
    private final BigDecimal predictedExpenseNextMonth;
}