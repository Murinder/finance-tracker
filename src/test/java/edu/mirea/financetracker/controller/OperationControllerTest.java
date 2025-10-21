package edu.mirea.financetracker.controller;

import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.enums.OperationType;
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
import static org.mockito.Mockito.*;
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
    void createOperation_returnsCreatedDto() throws Exception {
        OperationDto input = new OperationDto();
        input.setAmount(new BigDecimal("200"));
        input.setType(OperationType.INCOME);
        input.setCurrency("USD");

        OperationDto saved = new OperationDto();
        saved.setId(1L);
        saved.setAmount(new BigDecimal("200"));
        saved.setType(OperationType.INCOME);
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
    void updateOperation_returnsUpdatedDto() throws Exception {
        OperationDto input = new OperationDto();
        input.setId(1L);
        input.setAmount(new BigDecimal("300"));
        input.setCurrency("EUR");

        when(service.updateOperation(any())).thenReturn(input);

        mockMvc.perform(put("/api/operations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value("300"));
    }

    @Test
    void deleteOperation_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/operations/1"))
                .andExpect(status().isNoContent());
    }
}