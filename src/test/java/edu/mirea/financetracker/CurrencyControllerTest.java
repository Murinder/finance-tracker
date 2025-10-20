package edu.mirea.financetracker;

import edu.mirea.financetracker.controller.CurrencyController;
import edu.mirea.financetracker.dto.CurrencyRateDto;
import edu.mirea.financetracker.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService service;

    @Test
    void getRates_returnsCurrencyData() throws Exception {
        CurrencyRateDto dto = new CurrencyRateDto();
        dto.setBase_code("RUB");
        dto.setRates(Map.of("USD", 0.0113));

        when(service.getRatesInRub()).thenReturn(dto);

        mockMvc.perform(get("/api/currencies/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base_code").value("RUB"))
                .andExpect(jsonPath("$.rates.USD").value(0.0113));
    }
}