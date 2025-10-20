package edu.mirea.financetracker.controller;

import edu.mirea.financetracker.dto.CurrencyRateDto;
import edu.mirea.financetracker.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currencies")
@Tag(name = "Валюты", description = "Получение курсов валют относительно RUB")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/rates")
    @Operation(summary = "Получить курсы всех валют относительно RUB")
    public CurrencyRateDto getRates() {
        return currencyService.getRatesInRub();
    }
}