package com.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachment {
  private Long id;
  private Long taskId;
  private String fileName;
  private String storedFileName;
  private String contentType;
  private Long size;
  private LocalDateTime uploadedAt;
}