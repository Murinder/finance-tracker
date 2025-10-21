package edu.mirea.financetracker.mapper;

import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.entity.Operation;
import edu.mirea.financetracker.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OperationMapperTest {

    private final OperationMapper mapper = Mappers.getMapper(OperationMapper.class);

    @Test
    void testToEntity() {
        OperationDto dto = new OperationDto();
        dto.setAmount(new BigDecimal("100.50"));
        dto.setCategory("Food");
        dto.setType(OperationType.EXPENSE);
        dto.setDate(OffsetDateTime.now());
        dto.setCurrency("RUB");

        Operation entity = mapper.toEntity(dto);

        assertThat(entity.getAmount()).isEqualByComparingTo("100.50");
        assertThat(entity.getCategory()).isEqualTo("Food");
        assertThat(entity.getType()).isEqualTo(OperationType.EXPENSE);
        assertThat(entity.getCurrency()).isEqualTo("RUB");
    }

    @Test
    void testToDto() {
        Operation entity = new Operation();
        entity.setAmount(new BigDecimal("200"));
        entity.setCategory("Salary");
        entity.setType(OperationType.INCOME);
        entity.setDate(OffsetDateTime.now());
        entity.setCurrency("USD");

        OperationDto dto = mapper.toDto(entity);

        assertThat(dto.getAmount()).isEqualByComparingTo("200");
        assertThat(dto.getCategory()).isEqualTo("Salary");
        assertThat(dto.getType()).isEqualTo(OperationType.INCOME);
        assertThat(dto.getCurrency()).isEqualTo("USD");
    }
}