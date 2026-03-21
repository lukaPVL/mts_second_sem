package com.example.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttachmentResponseDto {
  private Long id;
  private String fileName;
  private Long size;
  private LocalDateTime uploadedAt;
}
