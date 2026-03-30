package com.example.service;

import com.example.enums.Priority;
import com.example.exception.TaskNotFoundException;
import com.example.entity.Task;
import com.example.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;


  @Test
  @DisplayName("Транзакция должна откатиться, если один из ID неверный")
  void bulkCompleteTasks_ShouldRollback_WhenOneIdIsInvalid() {
    Task task = taskRepository.save(Task.builder()
      .title("Before Rollback")
      .completed(false)
      .build());
    Long validId = task.getId();
    Long invalidId = -1L;

    assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(validId, invalidId)))
      .isInstanceOf(TaskNotFoundException.class);

    Task result = taskRepository.findById(validId).orElseThrow();
    assertThat(result.isCompleted())
      .as("Статус задачи не должен измениться из-за отката транзакции")
      .isFalse();
  }

  @Test
  @DisplayName("Поле createdAt должно заполняться автоматически (JPA Auditing)")
  void save_ShouldUseAuditing_ForCreatedAt() {
    Task task = Task.builder()
      .title("Auditing Test")
      .priority(Priority.LOW)
      .build();

    Task saved = taskService.save(task);

    assertThat(saved.getCreatedAt())
      .as("Дата создания должна быть установлена автоматически")
      .isNotNull();
  }

  @Test
  @DisplayName("Update должен выбрасывать исключение, если ID пустой")
  void update_ShouldThrowException_WhenIdIsNull() {
    Task task = Task.builder().title("No ID").build();

    assertThatThrownBy(() -> taskService.update(task))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Task id cannot be null");
  }
}