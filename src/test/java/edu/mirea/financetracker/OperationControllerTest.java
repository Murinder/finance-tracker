package edu.mirea.financetracker;

import edu.mirea.financetracker.controller.OperationController;
import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.service.OperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationController.class)
class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OperationService service;

    @Test
    void getAllOperations_returnsList() throws Exception {
        OperationDto dto = new OperationDto();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("100"));
        dto.setType("EXPENSE");
        dto.setCurrency("RUB");
        dto.setDate(OffsetDateTime.now());

        when(service.getAllOperations()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value("100"));
    }

    @Test
    void createOperation_savesAndReturns() throws Exception {
        OperationDto input = new OperationDto();
        input.setAmount(new BigDecimal("200"));
        input.setType("INCOME");
        input.setCurrency("USD");

        OperationDto saved = new OperationDto();
        saved.setId(1L);
        saved.setAmount(new BigDecimal("200"));
        saved.setType("INCOME");
        saved.setCurrency("USD");
        saved.setDate(OffsetDateTime.now());

        when(service.saveOperation(any())).thenReturn(saved);

        mockMvc.perform(post("/api/operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteOperation_callsService() throws Exception {
        mockMvc.perform(delete("/api/operations/1"))
                .andExpect(status().isNoContent());
    }
}