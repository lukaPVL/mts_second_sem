package com.example.dto;

import com.example.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDto {

  @Schema(description = "Идентификатор задачи",
    example = "1",
    accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Название задачи",
    example = "Купить молоко")
  private String title;

  @Schema(description = "Описание задачи",
    example = "Купить 2 литра молока")
  private String description;

  @Schema(description = "Статус выполнения",
    example = "false")
  private boolean completed;

  @Schema(description = "Дата и время создания",
    example = "2024-01-15T10:30:00",
    format = "date-time",
    accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "Дата выполнения",
    example = "2025-12-31",
    format = "date")
  private LocalDate dueDate;

  @Schema(description = "Приоритет задачи",
    example = "HIGH",
    allowableValues = {"LOW", "MEDIUM", "HIGH"})
  private Priority priority;

  @Schema(description = "Теги задачи",
    example = "[\"работа\", \"срочно\"]")
  private Set<String> tags;
}