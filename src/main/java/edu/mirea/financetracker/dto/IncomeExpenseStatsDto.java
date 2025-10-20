package edu.mirea.financetracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class IncomeExpenseStatsDto {
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
}