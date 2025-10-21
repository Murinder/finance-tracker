package edu.mirea.financetracker.controller;

import edu.mirea.financetracker.dto.CurrencyRateDto;
import edu.mirea.financetracker.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService service;

    @Test
    void getRates_returnsCurrencyData() throws Exception {
        CurrencyRateDto dto = CurrencyRateDto.builder().build();
        dto.setBaseCode("RUB");
        dto.setRates(Map.of("USD", 0.012, "EUR", 0.011));

        when(service.getRates()).thenReturn(dto);

        mockMvc.perform(get("/api/currencies/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base_code").value("RUB"))
                .andExpect(jsonPath("$.rates.USD").value(0.012));
    }

    @Test
    void setBaseCurrency_updatesBase() throws Exception {
        mockMvc.perform(post("/api/currencies/base-currency")
                .param("currency", "USD"))
                .andExpect(status().isOk());

        // Проверка вызова
        verify(service).setBaseCurrency("USD");
    }
}