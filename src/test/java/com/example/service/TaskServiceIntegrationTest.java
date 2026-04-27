package com.example.service;

import com.example.enums.Priority;
import com.example.exception.TaskNotFoundException;
import com.example.entity.Task;
import com.example.repository.TaskRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Временно отключено: тесты не адаптированы под новую систему безопасности")
class TaskServiceIntegrationTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  @Test
  void bulkCompleteTasks_ShouldRollbackOnException() {
    Task task = Task.builder()
      .title("Тестовая задача")
      .priority(Priority.LOW)
      .build();
    taskRepository.save(task);
    Long existingId = task.getId();
    Long nonExistingId = 999L;

    assertThrows(TaskNotFoundException.class, () -> {
      taskService.bulkCompleteTasks(List.of(existingId, nonExistingId));
    });

    Task resultTask = taskRepository.findById(existingId).get();
    assertThat(resultTask.isCompleted()).isFalse();
  }
}