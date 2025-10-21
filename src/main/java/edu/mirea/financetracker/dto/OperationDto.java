package edu.mirea.financetracker.dto;

import edu.mirea.financetracker.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OperationDto {
    private Long id;
    private BigDecimal amount;
    private String category;
    private OperationType type; // OperationType.INCOME or OperationType.EXPENSE
    private OffsetDateTime date;
    private String currency; // e.g., "USD", "EUR"
}