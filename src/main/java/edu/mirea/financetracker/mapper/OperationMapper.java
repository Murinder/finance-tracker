package edu.mirea.financetracker.mapper;

import edu.mirea.financetracker.dto.OperationDto;
import edu.mirea.financetracker.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface OperationMapper {

    Operation toEntity(OperationDto dto);

    OperationDto toDto(Operation entity);

    void updateEntityFromDto(OperationDto dto, @MappingTarget Operation entity);
}