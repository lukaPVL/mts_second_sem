package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

  @Schema(description = "Временная метка ошибки",
    example = "2024-01-15T10:30:00Z")
  private Instant timestamp;

  @Schema(description = "HTTP статус код",
    example = "400")
  private int status;

  @Schema(description = "Краткое описание ошибки",
    example = "Validation Failed")
  private String error;

  @Schema(description = "Детальное сообщение об ошибке",
    example = "Invalid request content")
  private String message;

  @Schema(description = "Путь запроса",
    example = "/api/tasks")
  private String path;

  @Schema(description = "Дополнительные детали ошибки")
  private Map<String, Object> details;
}