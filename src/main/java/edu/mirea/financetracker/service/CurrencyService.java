package edu.mirea.financetracker.service;

import edu.mirea.financetracker.dto.CurrencyRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final RestTemplate restTemplate;

    /**
     * Получает курсы валют относительно RUB с open.er-api.com
     */
    public CurrencyRateDto getRatesInRub() {
        String url = "https://open.er-api.com/v6/latest/RUB";
        return restTemplate.getForObject(url, CurrencyRateDto.class);
    }
}