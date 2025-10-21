package edu.mirea.financetracker.service;

import edu.mirea.financetracker.config.CurrencyValidator;
import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.entity.Operation;
import edu.mirea.financetracker.enums.OperationType;
import edu.mirea.financetracker.mapper.OperationMapper;
import edu.mirea.financetracker.mapper.OperationMapperImpl;
import edu.mirea.financetracker.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OperationServiceTest {

    @Mock
    private OperationRepository repository;

    @Spy
    private OperationMapper mapper = new OperationMapperImpl();

    @Mock
    private CurrencyService currencyService;

    @Mock
    private CurrencyValidator currencyValidator;

    @InjectMocks
    private OperationService service;

    @Test
    void saveOperation_validCurrency_saves() {
        OperationDto dto = new OperationDto();
        dto.setAmount(new BigDecimal("100"));
        dto.setType(OperationType.EXPENSE);
        dto.setCurrency("USD");
        dto.setDate(OffsetDateTime.now());

        when(currencyValidator.isValid("USD")).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OperationDto saved = service.saveOperation(dto);

        assertThat(saved.getAmount()).isEqualByComparingTo("100");
        verify(repository).save(any());
    }

    @Test
    void saveOperation_invalidCurrency_throws() {
        OperationDto dto = new OperationDto();
        dto.setCurrency("USSDD");

        when(currencyValidator.isValid("USSDD")).thenReturn(false);
        assertThatThrownBy(() -> service.saveOperation(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unsupported currency");
    }

    @Test
    void updateOperation_updatesEntity() {

        Long id = 1L;
        OperationDto dto = new OperationDto();
        dto.setId(id);
        dto.setAmount(new BigDecimal("200"));
        dto.setCurrency("EUR");

        Operation existing = new Operation();
        existing.setId(id);
        existing.setAmount(new BigDecimal("100"));
        existing.setCurrency("RUB");

        when(currencyValidator.isValid("EUR")).thenReturn(true);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OperationDto updated = service.updateOperation(dto);

        assertThat(updated.getAmount()).isEqualByComparingTo("200");
        assertThat(updated.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void getForecastForNextMonth_convertsToBaseCurrency() {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        Operation op1 = new Operation(null, new BigDecimal("100"), "Test", OperationType.EXPENSE, now, "RUB");
        Operation op2 = new Operation(null, new BigDecimal("10"), "Test", OperationType.EXPENSE, now, "USD");

        when(repository.findByTypeAndDateBetween(eq(OperationType.EXPENSE), any(), any()))
            .thenReturn(List.of(op1, op2));
        when(currencyService.convertToBaseCurrency(any(), eq("RUB")))
            .thenReturn(new BigDecimal("100"));
        when(currencyService.convertToBaseCurrency(any(), eq("USD")))
            .thenReturn(new BigDecimal("830")); // 10 USD ≈ 830 RUB

        // When
        var forecast = service.getForecastForNextMonth();

        // Then
        assertThat(forecast.getPredictedExpenseNextMonth()).isEqualByComparingTo("310.00");
    }
}