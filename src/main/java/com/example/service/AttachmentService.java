package com.example.service;

import com.example.model.TaskAttachment;
import com.example.repository.TaskAttachmentRepository;
import com.example.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

  private final TaskAttachmentRepository attachmentRepository;
  private final TaskRepository taskRepository;

  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
    taskRepository.findById(taskId)
        .orElseThrow(() -> new RuntimeException("Task with id: " + taskId + " not found :("));

    try {
      Path uploadPath = Paths.get(uploadDir);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      String originalFileName = file.getOriginalFilename();
      String storedFileName = UUID.randomUUID() + getFileExtension(originalFileName);

      Path filePath = uploadPath.resolve(storedFileName);
      Files.copy(file.getInputStream(), filePath);

      TaskAttachment attachment = TaskAttachment.builder()
        .taskId(taskId)
        .fileName(originalFileName)
        .storedFileName(storedFileName)
        .contentType(file.getContentType())
        .size(file.getSize())
        .uploadedAt(LocalDateTime.now())
        .build();

      return attachmentRepository.save(attachment);

    } catch (IOException e) {
      log.error("Failed to store file for task {}", taskId, e);
      throw new RuntimeException("Failed to store file", e);
    }
  }

  public TaskAttachment getAttachment(Long attachmentId) {
    return attachmentRepository.findById(attachmentId)
        .orElseThrow(() -> new RuntimeException("Attachment not found :( (attachmentId: " + attachmentId + ")"));
  }

  public Resource loadAsResource(Long attachmentId) {
    TaskAttachment attachment = getAttachment(attachmentId);

    try {
      Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName());
      Resource resource = new UrlResource(filePath.toUri());

      if (!resource.exists()) {
        throw new RuntimeException("File not found :( " + attachment.getStoredFileName());
      }

      return resource;
    } catch (IOException e) {
      throw new RuntimeException("Failed to load file", e);
    }
  }

  public void deleteAttachment(Long attachmentId) {
    TaskAttachment attachment = getAttachment(attachmentId);
    try {
      Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName());
      Files.deleteIfExists(filePath);

      attachmentRepository.deleteById(attachmentId);
    } catch (IOException e) {
      log.error("Failed to delete file for attachment {}", attachmentId, e);
      throw new RuntimeException("Failed to delete file", e);
    }
  }

  public List<TaskAttachment> getAttachmentsByTaskId(Long taskId) {
    taskRepository.findById(taskId)
        .orElseThrow(() -> new RuntimeException("Task with id: " + taskId + " not found :("));

    return attachmentRepository.findByTaskId(taskId);
  }


  private String getFileExtension(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      return "";
    }
    return fileName.substring(fileName.lastIndexOf("."));
  }
}
