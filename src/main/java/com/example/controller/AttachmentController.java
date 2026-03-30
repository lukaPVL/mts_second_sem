package com.example.controller;

import com.example.dto.AttachmentResponseDto;
import com.example.exception.TaskNotFoundException;
import com.example.model.TaskAttachment;
import com.example.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttachmentController {

  private final AttachmentService attachmentService;

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

  @PostMapping("/tasks/{taskId}/attachments")
  public ResponseEntity<?> uploadAttachment(
    @PathVariable Long taskId,
    @RequestParam("file") MultipartFile file) {

    try {
      TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);

      AttachmentResponseDto responseDto = AttachmentResponseDto.builder()
        .id(attachment.getId())
        .fileName(attachment.getFileName())
        .size(attachment.getSize())
        .uploadedAt(attachment.getUploadedAt())
        .build();

      return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .body(responseDto);
    } catch (TaskNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header("X-API-Version", apiVersion)
        .build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("X-API-Version", apiVersion)
        .build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .header("X-API-Version", apiVersion)
        .build();
    }
  }

  @GetMapping("/attachments/{attachmentId}")
  public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
    Resource resource = attachmentService.loadAsResource(attachmentId);
    TaskAttachment attachment = attachmentService.getAttachment(attachmentId);

    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(attachment.getContentType()))
      .header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + attachment.getFileName() + "\"")
      .header("X-API-Version", apiVersion)
      .body(resource);
  }

  @DeleteMapping("/attachments/{attachmentId}")
  public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
    attachmentService.deleteAttachment(attachmentId);
    return ResponseEntity.noContent()
      .header("X-API-Version", apiVersion)
      .build();
  }

  @GetMapping("/tasks/{taskId}/attachments")
  public ResponseEntity<List<AttachmentResponseDto>> getAttachments(@PathVariable Long taskId) {
    List<TaskAttachment> attachments = attachmentService.getAttachmentsByTaskId(taskId);

    List<AttachmentResponseDto> responseDtoList = attachments.stream()
        .map(attachment -> AttachmentResponseDto.builder()
          .id(attachment.getId())
          .fileName(attachment.getFileName())
          .size(attachment.getSize())
          .uploadedAt(attachment.getUploadedAt())
          .build())
      .toList();

    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .body(responseDtoList);
  }
}
