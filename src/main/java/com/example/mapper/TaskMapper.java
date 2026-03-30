package com.example.mapper;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
  componentModel = "spring",
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "completed", constant = "false")
  Task toEntity(TaskCreateDto dto);

  TaskResponseDto toResponseDto(Task task);

  void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);
}