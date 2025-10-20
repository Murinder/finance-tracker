package edu.mirea.financetracker.service;

import edu.mirea.financetracker.dto.BalanceByCurrencyDto;
import edu.mirea.financetracker.dto.ForecastDto;
import edu.mirea.financetracker.dto.IncomeExpenseStatsDto;
import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.entity.Operation;
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
        BigDecimal income = operationRepository.getTotalIncome(from, to);
        BigDecimal expense = operationRepository.getTotalExpense(from, to);
        return new IncomeExpenseStatsDto(
                income != null ? income : BigDecimal.ZERO,
                expense != null ? expense : BigDecimal.ZERO
        );
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
        OffsetDateTime start = now.minusMonths(3).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime end = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        BigDecimal avgExpense = operationRepository.getTotalExpense(start, end);
        if (avgExpense == null) avgExpense = BigDecimal.ZERO;

        long months = ChronoUnit.MONTHS.between(start, end);
        if (months == 0) months = 1;

        BigDecimal monthlyAvg = avgExpense.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        return new ForecastDto(monthlyAvg);
    }
}