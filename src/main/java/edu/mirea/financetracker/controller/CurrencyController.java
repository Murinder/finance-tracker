package edu.mirea.financetracker.controller;

import edu.mirea.financetracker.dto.CurrencyRateDto;
import edu.mirea.financetracker.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currencies")
@Tag(name = "Валюты", description = "Получение курсов валют относительно базовой валюты")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/rates")
    @Operation(summary = "Получить курсы всех валют относительно базовой валюты")
    public CurrencyRateDto getRates() {
        return currencyService.getRates();
    }

    @PostMapping("/base-currency")
    @Operation(summary = "Установить базовую валюту для расчётов")
    public void setBaseCurrency(@RequestParam String currency) {
        currencyService.setBaseCurrency(currency);
    }
}