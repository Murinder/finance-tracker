package edu.mirea.financetracker.entity;

import edu.mirea.financetracker.enums.OperationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "operations")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    private String category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType type;

    @Column(name = "date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime date;

    @Column(nullable = false)
    private String currency;
}