package edu.mirea.financetracker;

import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.entity.Operation;
import edu.mirea.financetracker.mapper.OperationMapper;
import edu.mirea.financetracker.mapper.OperationMapperImpl;
import edu.mirea.financetracker.repository.OperationRepository;
import edu.mirea.financetracker.service.OperationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @Mock
    private OperationRepository repository;

    @Spy
    private OperationMapper mapper = Mappers.getMapper(OperationMapper.class); // MapStruct генерирует реализацию

    @InjectMocks
    private OperationService service;

    @Test
    void saveOperation_setsTimestampIfNull() {
        OperationDto dto = new OperationDto();
        dto.setAmount(new BigDecimal("100"));
        dto.setType("EXPENSE");
        dto.setCurrency("RUB");

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OperationDto saved = service.saveOperation(dto);

        assertThat(saved.getDate()).isNotNull();
        verify(repository).save(any());
    }

    @Test
    void deleteOperation_callsRepository() {
        service.deleteOperation(1L);
        verify(repository).deleteById(1L);
    }
}