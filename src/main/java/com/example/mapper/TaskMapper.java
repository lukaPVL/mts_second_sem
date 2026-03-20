package com.example.mapper;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
  componentModel = "spring",
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {

  Task toEntity(TaskCreateDto dto);

  TaskResponseDto toResponseDto(Task dto);

  Task updateEntity(TaskUpdateDto dto, @MappingTarget Task task);
}
