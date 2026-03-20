package com.example.dto;

import com.example.enums.Priority;
import jakarta.validation.constraints.FutureOrPresent;
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

  @Size(min = 3, max = 100, groups = OnCreate.class)
  private String title;

  @Size(max = 500, groups = OnCreate.class)
  private String descriptions;

  @FutureOrPresent(groups = OnCreate.class)
  private LocalDate dueDate;

  @NotNull(groups = OnCreate.class)
  private Priority priority;

  @Size(max = 5, groups = OnCreate.class)
  private Set<String> tags;
}
