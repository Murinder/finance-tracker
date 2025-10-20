package edu.mirea.financetracker.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OperationDto {
    private Long id;
    private BigDecimal amount;
    private String category;
    private String type; // "INCOME" or "EXPENSE"
    private OffsetDateTime date;
    private String currency; // e.g., "USD", "EUR"
}