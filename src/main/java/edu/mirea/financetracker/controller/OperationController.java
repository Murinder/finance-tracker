package edu.mirea.financetracker.controller;

import edu.mirea.financetracker.dto.*;
import edu.mirea.financetracker.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/operations")
@Tag(name = "Операции", description = "Управление финансовыми операциями")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping
    @Operation(summary = "Получить все операции")
    public List<OperationDto> getAll() {
        return operationService.getAllOperations();
    }

    @GetMapping("/period")
    @Operation(summary = "Получить операции за период")
    public List<OperationDto> getByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return operationService.getOperationsBetween(from, to);
    }

    @PostMapping
    @Operation(summary = "Добавить новую операцию")
    public OperationDto create(@RequestBody OperationDto operationDto) {
        return operationService.saveOperation(operationDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить операцию по ID")
    public void delete(@PathVariable Long id) {
        operationService.deleteOperation(id);
    }

    // === Статистика ===
    @GetMapping("/stats/income-expense")
    @Operation(summary = "Статистика доходов и расходов за период")
    public IncomeExpenseStatsDto getIncomeExpenseStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return operationService.getIncomeExpenseStats(from, to);
    }

    @GetMapping("/stats/balance")
    @Operation(summary = "Баланс по валютам")
    public List<BalanceByCurrencyDto> getBalanceByCurrency() {
        return operationService.getBalanceByCurrency();
    }

    @GetMapping("/stats/forecast")
    @Operation(summary = "Прогноз расходов на следующий месяц")
    public ForecastDto getForecast() {
        return operationService.getForecastForNextMonth();
    }
}