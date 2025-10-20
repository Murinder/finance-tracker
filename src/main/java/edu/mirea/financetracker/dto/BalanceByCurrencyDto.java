package edu.mirea.financetracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BalanceByCurrencyDto {
    private final String currency;
    private final BigDecimal balance;
}