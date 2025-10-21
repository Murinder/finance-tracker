package edu.mirea.financetracker.service;

import edu.mirea.financetracker.config.CurrencyValidator;
import edu.mirea.financetracker.dto.CurrencyRateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test") 
class CurrencyServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyValidator currencyValidator;

    @BeforeEach
    void setUp() {
        // Create a mock for ValueOperations
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);

        currencyValidator.updateSupportedCurrencies(Set.of("RUB", "USD", "EUR"));

        // Mock opsForValue() to return the mock ValueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        // Now mock the behavior of valueOps.get() and set()
        when(valueOps.get("exchange_rates")).thenReturn(null);
        doNothing().when(valueOps).set(anyString(), any(), any());

        // Mock RestTemplate as before
        when(restTemplate.getForObject(argThat((String url) -> url != null && url.contains("/latest/RUB")), eq(CurrencyRateDto.class)))
                .thenReturn(CurrencyRateDto.builder()
                        .baseCode("RUB")
                        .rates(Map.of("RUB", 1.0, "USD", 0.012, "EUR", 0.011))
                        .build());

        when(restTemplate.getForObject(argThat((String url) -> url != null && url.contains("/latest/USD")), eq(CurrencyRateDto.class)))
                .thenReturn(CurrencyRateDto.builder()
                        .baseCode("USD")
                        .rates(Map.of("RUB", 83.33, "USD", 1.0, "EUR", 0.92))
                        .build());
    }

    @Test
    void getRatesInRub_loadsFromApiAndUpdatesValidator() {
        Map<String, Double> rates = currencyService.getRates().getRates();

        assertThat(rates).containsKey("USD");
        assertThat(currencyValidator.isValid("USD")).isTrue();
        assertThat(currencyValidator.isValid("INVALID")).isFalse();
    }

    @Test
    void convertToBaseCurrency_RUB_to_USD() {
        currencyService.setBaseCurrency("USD");
        BigDecimal result = currencyService.convertToBaseCurrency(new BigDecimal("100"), "RUB");
        // 100 RUB * 0.012 = 1.2 USD
        assertThat(result).isEqualByComparingTo("1.20");
    }

    @Test
    void convertToBaseCurrency_sameCurrency_returnsOriginal() {
        currencyService.setBaseCurrency("RUB");
        BigDecimal result = currencyService.convertToBaseCurrency(new BigDecimal("50"), "RUB");
        assertThat(result).isEqualByComparingTo("50");
    }

    @Test
    void convertToBaseCurrency_unsupportedCurrency_throws() {
        assertThatThrownBy(() -> currencyService.convertToBaseCurrency(new BigDecimal("10"), "XYZ"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown currency");
    }
}