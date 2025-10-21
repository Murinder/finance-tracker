package edu.mirea.financetracker.service;

import edu.mirea.financetracker.config.CurrencyValidator;
import edu.mirea.financetracker.dto.CurrencyRateDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AtomicReference<String> baseCurrency;
    private final CurrencyValidator currencyValidator;

    public void setBaseCurrency(String currency) {
        this.baseCurrency.set(currency);
    }

    public String getBaseCurrency() {
        return baseCurrency.get();
    }

    @PostConstruct
    public void initSupportedCurrencies() {
        try {
            Map<String, Double> rates = getRates().getRates();
            currencyValidator.updateSupportedCurrencies(rates.keySet());
            log.info("Initialized with {} supported currencies", rates.size());
        } catch (Exception e) {
            log.warn("Failed to initialize currency list on startup", e);
        }
    }

    @SuppressWarnings("unchecked")
    public CurrencyRateDto getRates() {
        String cacheKey = getBaseCurrency();
        Map<String, Double> rates = (Map<String, Double>) redisTemplate.opsForValue().get(cacheKey);
        if (rates == null) {
            String url = String.format("https://open.er-api.com/v6/latest/%s", getBaseCurrency());
            CurrencyRateDto dto = restTemplate.getForObject(url, CurrencyRateDto.class);
            if (dto == null) throw new RuntimeException("Failed to fetch rates");
            rates = dto.getRates();
            redisTemplate.opsForValue().set(cacheKey, rates, Duration.ofMinutes(30));

        }
        return CurrencyRateDto.builder()
                .rates(rates)
                .baseCode(getBaseCurrency())
                .build();
    }

    public BigDecimal convertToBaseCurrency(BigDecimal amount, String fromCurrency) {
        String base = getBaseCurrency();
        if (fromCurrency.equals(base)) {
            return amount;
        }

        CurrencyRateDto dto = getRates();
        Map<String, Double> rates = dto.getRates();

        Double rateFromBaseToFrom = rates.get(fromCurrency);
        if (rateFromBaseToFrom == null) {
            throw new IllegalArgumentException("Unknown currency: " + fromCurrency);
        }

        BigDecimal rate = BigDecimal.valueOf(1.0).divide(
                BigDecimal.valueOf(rateFromBaseToFrom),
                10,
                RoundingMode.HALF_UP
        );

        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}