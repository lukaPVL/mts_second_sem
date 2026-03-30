package com.example.service;

import com.example.dto.TaskCreateDto;
import com.example.enums.Priority;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.model.TaskAttachment;
import com.example.repository.TaskAttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AttachmentServiceTest {

  @Autowired
  private AttachmentService attachmentService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskMapper taskMapper;

  @Autowired
  private TaskAttachmentRepository attachmentRepository;

  private Long taskId;

  @BeforeEach
  void setUp() {
    // Создаём задачу для тестов
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Task for Attachments");
    createDto.setPriority(Priority.MEDIUM);

    Task task = taskMapper.toEntity(createDto);
    task.setCreatedAtNow();
    Task savedTask = taskService.save(task);
    taskId = savedTask.getId();
  }

  private MockMultipartFile createMockFile(String name, String content, String contentType) {
    return new MockMultipartFile(
      "file",
      name,
      contentType,
      content.getBytes()
    );
  }

  @Test
  void storeAttachment_ShouldSaveFileAndMetadata_WhenValid() throws IOException {
    MockMultipartFile file = createMockFile("test.txt", "Hello World", "text/plain");

    TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);

    assertThat(attachment).isNotNull();
    assertThat(attachment.getId()).isNotNull();
    assertThat(attachment.getTaskId()).isEqualTo(taskId);
    assertThat(attachment.getFileName()).isEqualTo("test.txt");
    assertThat(attachment.getStoredFileName()).endsWith(".txt");
    assertThat(attachment.getContentType()).isEqualTo("text/plain");
    assertThat(attachment.getSize()).isEqualTo(11);
    assertThat(attachment.getUploadedAt()).isNotNull();

    // Проверяем, что файл действительно сохранён
    Path filePath = Paths.get("uploads", attachment.getStoredFileName());
    assertThat(Files.exists(filePath)).isTrue();

    // Очистка
    Files.deleteIfExists(filePath);
  }

  @Test
  void storeAttachment_ShouldThrowException_WhenTaskNotFound() {
    MockMultipartFile file = createMockFile("test.txt", "Hello", "text/plain");

    assertThatThrownBy(() -> attachmentService.storeAttachment(99999L, file))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Task with id: 99999 not found");
  }

  @Test
  void storeAttachment_ShouldThrowException_WhenFileIsEmpty() {
    MockMultipartFile emptyFile = createMockFile("empty.txt", "", "text/plain");

    assertThatThrownBy(() -> attachmentService.storeAttachment(taskId, emptyFile))
      .isInstanceOf(RuntimeException.class);
  }

  @Test
  void getAttachment_ShouldReturnAttachment_WhenExists() throws IOException {
    MockMultipartFile file = createMockFile("test.txt", "Content", "text/plain");
    TaskAttachment savedAttachment = attachmentService.storeAttachment(taskId, file);

    TaskAttachment foundAttachment = attachmentService.getAttachment(savedAttachment.getId());

    assertThat(foundAttachment).isNotNull();
    assertThat(foundAttachment.getId()).isEqualTo(savedAttachment.getId());
    assertThat(foundAttachment.getFileName()).isEqualTo("test.txt");

    // Очистка
    Path filePath = Paths.get("uploads", savedAttachment.getStoredFileName());
    Files.deleteIfExists(filePath);
  }

  @Test
  void getAttachment_ShouldThrowException_WhenNotFound() {
    assertThatThrownBy(() -> attachmentService.getAttachment(99999L))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Attachment not found");
  }

  @Test
  void loadAsResource_ShouldReturnResource_WhenFileExists() throws IOException {
    MockMultipartFile file = createMockFile("test.txt", "Hello World", "text/plain");
    TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);

    Resource resource = attachmentService.loadAsResource(attachment.getId());

    assertThat(resource).isNotNull();
    assertThat(resource.exists()).isTrue();
    assertThat(resource.getFilename()).isEqualTo(attachment.getStoredFileName());

    // Очистка
    Path filePath = Paths.get("uploads", attachment.getStoredFileName());
    Files.deleteIfExists(filePath);
  }

  @Test
  void loadAsResource_ShouldThrowException_WhenFileNotExists() {
    // Создаём метаданные, но файл не существует
    TaskAttachment attachment = TaskAttachment.builder()
      .id(1L)
      .taskId(taskId)
      .fileName("ghost.txt")
      .storedFileName("ghost.txt")
      .build();
    attachmentRepository.save(attachment);

    assertThatThrownBy(() -> attachmentService.loadAsResource(attachment.getId()))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("File not found");
  }

  @Test
  void getAttachmentsByTaskId_ShouldReturnList_WhenAttachmentsExist() throws IOException {
    MockMultipartFile file1 = createMockFile("file1.txt", "Content 1", "text/plain");
    MockMultipartFile file2 = createMockFile("file2.pdf", "PDF Content", "application/pdf");

    TaskAttachment attachment1 = attachmentService.storeAttachment(taskId, file1);
    TaskAttachment attachment2 = attachmentService.storeAttachment(taskId, file2);

    List<TaskAttachment> attachments = attachmentService.getAttachmentsByTaskId(taskId);

    assertThat(attachments).hasSize(2);
    assertThat(attachments).extracting("fileName")
      .containsExactlyInAnyOrder("file1.txt", "file2.pdf");

    // Очистка
    Path filePath1 = Paths.get("uploads", attachment1.getStoredFileName());
    Path filePath2 = Paths.get("uploads", attachment2.getStoredFileName());
    Files.deleteIfExists(filePath1);
    Files.deleteIfExists(filePath2);
  }

  @Test
  void getAttachmentsByTaskId_ShouldReturnEmptyList_WhenNoAttachments() {
    List<TaskAttachment> attachments = attachmentService.getAttachmentsByTaskId(taskId);

    assertThat(attachments).isEmpty();
  }

  @Test
  void getAttachmentsByTaskId_ShouldThrowException_WhenTaskNotFound() {
    assertThatThrownBy(() -> attachmentService.getAttachmentsByTaskId(99999L))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Task with id: 99999 not found");
  }

  @Test
  void deleteAttachment_ShouldDeleteFileAndMetadata_WhenExists() throws IOException {
    MockMultipartFile file = createMockFile("toDelete.txt", "Delete me", "text/plain");
    TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);

    Path filePath = Paths.get("uploads", attachment.getStoredFileName());
    assertThat(Files.exists(filePath)).isTrue();

    attachmentService.deleteAttachment(attachment.getId());

    // Проверяем, что файл удалён
    assertThat(Files.exists(filePath)).isFalse();

    // Проверяем, что метаданные удалены
    assertThatThrownBy(() -> attachmentService.getAttachment(attachment.getId()))
      .isInstanceOf(RuntimeException.class);
  }

  @Test
  void deleteAttachment_ShouldThrowException_WhenAttachmentNotFound() {
    assertThatThrownBy(() -> attachmentService.deleteAttachment(99999L))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Attachment not found");
  }
}