package com.example.dto;

import com.example.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDto {

  @Schema(description = "Название задачи",
    example = "Купить молоко",
    minLength = 3,
    maxLength = 100,
    requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(groups = OnCreate.class)
  @Size(min = 3, max = 100, groups = OnCreate.class)
  private String title;

  @Schema(description = "Описание задачи",
    example = "Купить 2 литра молока",
    maxLength = 500)
  @Size(max = 500, groups = OnCreate.class)
  private String description;

  @Schema(description = "Дата выполнения",
    example = "2025-12-31",
    format = "date")
  @FutureOrPresent(groups = OnCreate.class)
  private LocalDate dueDate;

  @Schema(description = "Приоритет задачи",
    example = "HIGH",
    allowableValues = {"LOW", "MEDIUM", "HIGH"},
    requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(groups = OnCreate.class)
  private Priority priority;

  @Schema(description = "Теги задачи",
    example = "[\"работа\", \"срочно\"]",
    maxLength = 5)
  @Size(max = 5, groups = OnCreate.class)
  private Set<String> tags;
}