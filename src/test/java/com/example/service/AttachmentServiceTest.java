package com.example.service;

import com.example.entity.Task;
import com.example.entity.TaskAttachment;
import com.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttachmentServiceTest {

  @Autowired
  private AttachmentService attachmentService;

  @Autowired
  private TaskRepository taskRepository;

  private Task testTask;

  @BeforeEach
  void setUp() {
    // Создаем родительскую задачу
    testTask = taskRepository.save(Task.builder()
      .title("Integration Test Task")
      .build());
  }

  @Test
  void storeAttachment_ShouldSaveFileAndMetadata() throws IOException {
    MockMultipartFile file = new MockMultipartFile(
      "file", "test.txt", "text/plain", "Hello Spring".getBytes()
    );

    TaskAttachment saved = attachmentService.storeAttachment(testTask.getId(), file);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getFileName()).isEqualTo("test.txt");
    assertThat(saved.getTask().getId()).isEqualTo(testTask.getId());

    Path path = Paths.get("uploads", saved.getStoredFileName());
    assertThat(Files.exists(path)).isTrue();

    Files.deleteIfExists(path);
  }
}