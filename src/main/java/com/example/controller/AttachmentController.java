package com.example.controller;

import com.example.model.AttachmentResponseDto;
import com.example.model.TaskAttachment;
import com.example.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

  @PostMapping("tasks/{taskId}/attachments")
  public ResponseEntity<AttachmentResponseDto> uploadAttachment(
      @PathVariable Long taskId,
      @RequestParam("file") MultipartFile file) {

    TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);

    AttachmentResponseDto responseDto = AttachmentResponseDto.builder()
      .id(taskId)
      .fileName(attachment.getFileName())
      .size(attachment.getSize())
      .uploadedAt(attachment.getUploadedAt())
      .build();

    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .body(responseDto);
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

  @GetMapping("/task/{taskId}/attachments")
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
