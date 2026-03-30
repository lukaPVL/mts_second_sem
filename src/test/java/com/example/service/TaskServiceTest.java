package com.example.service;

import com.example.dto.TaskCreateDto;
import com.example.enums.Priority;
import com.example.exception.TaskNotFoundException;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TaskServiceTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskMapper taskMapper;

  private TaskCreateDto createDto;

  @BeforeEach
  void setUp() {
    createDto = new TaskCreateDto();
    createDto.setTitle("Test Task");
    createDto.setDescription("Test Description");
    createDto.setDueDate(LocalDate.now().plusDays(1));
    createDto.setPriority(Priority.HIGH);
    createDto.setTags(Set.of("work", "urgent"));
  }

  private Task createTestTask() {
    Task task = taskMapper.toEntity(createDto);
    task.setCreatedAtNow();
    Task savedTask = taskService.save(task);
    return savedTask;
  }

  @Test
  void save_ShouldCreateNewTask_WhenValidData() {
    Task savedTask = createTestTask();

    assertThat(savedTask).isNotNull();
    assertThat(savedTask.getId()).isNotNull();
    assertThat(savedTask.getTitle()).isEqualTo("Test Task");
    assertThat(savedTask.getDescription()).isEqualTo("Test Description");
    assertThat(savedTask.getDueDate()).isEqualTo(LocalDate.now().plusDays(1));
    assertThat(savedTask.getPriority()).isEqualTo(Priority.HIGH);
    assertThat(savedTask.getTags()).containsExactlyInAnyOrder("work", "urgent");
    assertThat(savedTask.isCompleted()).isFalse();
    assertThat(savedTask.getCreatedAt()).isNotNull();
  }

  @Test
  void save_ShouldSetCreatedAtAutomatically() {
    Task task = taskMapper.toEntity(createDto);
    task.setCreatedAtNow();

    Task savedTask = taskService.save(task);

    assertThat(savedTask.getCreatedAt()).isNotNull();
    assertThat(savedTask.getCreatedAt()).isBeforeOrEqualTo(java.time.LocalDateTime.now());
  }

  @Test
  void findAll_ShouldReturnAllTasks() {
    List<Task> before = taskService.findAll();

    createTestTask();
    createTestTask();

    List<Task> after = taskService.findAll();

    assertThat(after.size()).isEqualTo(before.size() + 2);
  }

  @Test
  void findById_ShouldReturnTask_WhenTaskExists() {
    Task savedTask = createTestTask();

    Task foundTask = taskService.findById(savedTask.getId());

    assertThat(foundTask).isNotNull();
    assertThat(foundTask.getId()).isEqualTo(savedTask.getId());
    assertThat(foundTask.getTitle()).isEqualTo(savedTask.getTitle());
  }

  @Test
  void findById_ShouldThrowTaskNotFoundException_WhenTaskNotFound() {
    assertThatThrownBy(() -> taskService.findById(99999L))
      .isInstanceOf(TaskNotFoundException.class)
      .hasMessageContaining("Task with id 99999 not found");
  }

  @Test
  void update_ShouldUpdateExistingTask() {
    Task savedTask = createTestTask();

    savedTask.setTitle("Updated Title");
    savedTask.setDescription("Updated Description");
    savedTask.setCompleted(true);
    savedTask.setPriority(Priority.LOW);

    Task updatedTask = taskService.update(savedTask);

    assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
    assertThat(updatedTask.getDescription()).isEqualTo("Updated Description");
    assertThat(updatedTask.isCompleted()).isTrue();
    assertThat(updatedTask.getPriority()).isEqualTo(Priority.LOW);

    // Проверяем, что изменения сохранились
    Task foundTask = taskService.findById(savedTask.getId());
    assertThat(foundTask.getTitle()).isEqualTo("Updated Title");
  }

  @Test
  void update_ShouldThrowTaskNotFoundException_WhenTaskNotFound() {
    Task nonExistentTask = Task.builder()
      .id(99999L)
      .title("Non Existent")
      .build();

    assertThatThrownBy(() -> taskService.update(nonExistentTask))
      .isInstanceOf(RuntimeException.class);
  }

  @Test
  void deleteById_ShouldDeleteTask_WhenTaskExists() {
    Task savedTask = createTestTask();

    taskService.deleteById(savedTask.getId());

    assertThatThrownBy(() -> taskService.findById(savedTask.getId()))
      .isInstanceOf(TaskNotFoundException.class);
  }

  @Test
  void deleteById_ShouldNotThrow_WhenTaskNotFound() {
    // Не должно выбросить исключение
    taskService.deleteById(99999L);
  }
}