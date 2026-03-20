package com.example.dto;

import com.example.enums.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDto {

  @Size(min = 3, max = 100, groups = OnUpdate.class)
  private String title;

  @Size(max = 500, groups = OnUpdate.class)
  private String descriptions;

  private Boolean completed;

  @FutureOrPresent(groups = OnUpdate.class)
  private LocalDate dueDate;

  private Priority priority;

  @Size(max = 5, groups = OnUpdate.class)
  private Set<String> tags;
}
