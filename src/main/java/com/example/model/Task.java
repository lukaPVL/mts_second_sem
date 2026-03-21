package com.example.model;

import com.example.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
  private Long id;
  private String title;
  private String description;
  private boolean completed;
  private LocalDateTime createdAt;
  private LocalDate dueDate;
  private Priority priority;

  @Builder.Default
  private Set<String> tags = new HashSet<>();

  public void setCreatedAtNow() {
    this.createdAt = LocalDateTime.now();
  }
}