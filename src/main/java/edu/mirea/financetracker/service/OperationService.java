package edu.mirea.financetracker.service;

import edu.mirea.financetracker.config.CurrencyValidator;
import edu.mirea.financetracker.dto.BalanceByCurrencyDto;
import edu.mirea.financetracker.dto.ForecastDto;
import edu.mirea.financetracker.dto.IncomeExpenseStatsDto;
import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.entity.Operation;
import edu.mirea.financetracker.enums.OperationType;
import edu.mirea.financetracker.mapper.OperationMapper;
import edu.mirea.financetracker.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;
    private final CurrencyService currencyService;
    private final CurrencyValidator currencyValidator;


    public List<OperationDto> getAllOperations() {
        return operationRepository.findAll().stream()
                .map(operationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<OperationDto> getOperationsBetween(OffsetDateTime from, OffsetDateTime to) {
        return operationRepository.findByDateBetween(from, to).stream()
                .map(operationMapper::toDto)
                .collect(Collectors.toList());
    }

    public OperationDto saveOperation(OperationDto operationDto) {
        validateCurrency(operationDto.getCurrency());
        Operation entity = operationMapper.toEntity(operationDto);
        if (entity.getDate() == null) {
            entity.setDate(OffsetDateTime.now());
        }
        Operation saved = operationRepository.save(entity);
        return operationMapper.toDto(saved);
    }

    public void deleteOperation(Long id) {
        operationRepository.deleteById(id);
    }

    // === Статистика ===
    public IncomeExpenseStatsDto getIncomeExpenseStats(OffsetDateTime from, OffsetDateTime to) {
        // Получаем все операции за период
        List<Operation> operations = operationRepository.findByDateBetween(from, to);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Operation op : operations) {
            BigDecimal amountInBase = currencyService.convertToBaseCurrency(op.getAmount(), op.getCurrency());
            if (OperationType.INCOME.equals(op.getType())) {
                totalIncome = totalIncome.add(amountInBase);
            } else if (OperationType.EXPENSE.equals(op.getType())) {
                totalExpense = totalExpense.add(amountInBase);
            }
        }

        return new IncomeExpenseStatsDto(totalIncome, totalExpense);
    }

    public List<BalanceByCurrencyDto> getBalanceByCurrency() {
        List<Object[]> results = operationRepository.getBalanceByCurrency();
        return results.stream()
                .map(row -> new BalanceByCurrencyDto((String) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());
    }

    // === Прогноз на следующий месяц ===
    public ForecastDto getForecastForNextMonth() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = now.minusMonths(3).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime end = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);

        // Получаем все расходы за период
        List<Operation> expenses = operationRepository.findByTypeAndDateBetween(OperationType.EXPENSE, start, end);

        BigDecimal totalInBase = BigDecimal.ZERO;
        for (Operation op : expenses) {
            totalInBase = totalInBase.add(
                    currencyService.convertToBaseCurrency(op.getAmount(), op.getCurrency())
            );
        }

        long months = ChronoUnit.MONTHS.between(start, end);
        if (months == 0) months = 1;

        BigDecimal avg = totalInBase.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        return new ForecastDto(avg);
    }

    public OperationDto updateOperation(OperationDto dto) {
        validateCurrency(dto.getCurrency());
        Operation entity = operationRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Operation not found"));
        entity.setAmount( dto.getAmount() != null ? dto.getAmount(): entity.getAmount());
        entity.setCategory( dto.getCategory() != null ? dto.getCategory(): entity.getCategory());
        entity.setType( dto.getType() != null ? dto.getType(): entity.getType() );
        entity.setDate( dto.getDate() != null ? dto.getDate(): entity.getDate());
        entity.setCurrency( dto.getCurrency() != null ? dto.getCurrency(): entity.getCurrency());
        Operation updated = operationRepository.save(entity);
        return operationMapper.toDto(updated);
    }

    public void validateCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (!currencyValidator.isValid(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency +
                    ". Supported currencies: " + currencyValidator.getSupportedCurrencies());
        }
    }

}