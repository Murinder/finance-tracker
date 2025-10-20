package edu.mirea.financetracker;

import edu.mirea.financetracker.dto.CurrencyRateDto;
import edu.mirea.financetracker.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService service;

    @Test
    void getRatesInRub_returnsCorrectData() {
        CurrencyRateDto mockResponse = new CurrencyRateDto();
        mockResponse.setBase_code("RUB");
        mockResponse.setRates(Map.of("USD", 0.0113, "EUR", 0.0104));

        when(restTemplate.getForObject("https://open.er-api.com/v6/latest/RUB", CurrencyRateDto.class))
                .thenReturn(mockResponse);

        CurrencyRateDto result = service.getRatesInRub();

        assertThat(result.getBase_code()).isEqualTo("RUB");
        assertThat(result.getRates()).containsEntry("USD", 0.0113);
    }
}