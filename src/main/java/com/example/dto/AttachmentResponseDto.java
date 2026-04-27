package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttachmentResponseDto {

  @Schema(description = "Идентификатор вложения",
    example = "1",
    accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Оригинальное имя файла",
    example = "document.pdf")
  private String fileName;

  @Schema(description = "Размер файла в байтах",
    example = "102400")
  private Long size;

  @Schema(description = "Дата и время загрузки",
    example = "2024-01-15T10:30:00",
    format = "date-time")
  private LocalDateTime uploadedAt;
}