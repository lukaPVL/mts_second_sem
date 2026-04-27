package com.example.mapper;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.enums.Priority;
import com.example.entity.Task;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Disabled("Временно отключено: тесты не адаптированы под новую систему безопасности")
class TaskMapperTest {

  @Autowired
  private TaskMapper taskMapper;

  @Test
  void toEntity_ShouldMapCreateDtoToTask() {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Test Task");
    dto.setDescription("Test Description");
    dto.setDueDate(LocalDate.of(2025, 12, 31));
    dto.setPriority(Priority.HIGH);
    dto.setTags(Set.of("work", "urgent"));

    Task task = taskMapper.toEntity(dto);

    assertThat(task).isNotNull();
    assertThat(task.getId()).isNull();
    assertThat(task.getTitle()).isEqualTo("Test Task");
    assertThat(task.getDescription()).isEqualTo("Test Description");
    assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
    assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
    assertThat(task.getTags()).containsExactlyInAnyOrder("work", "urgent");
    assertThat(task.isCompleted()).isFalse();
  }

  @Test
  void toResponseDto_ShouldMapTaskToResponseDto() {
    Task task = Task.builder()
      .id(1L)
      .title("Test Task")
      .description("Test Description")
      .completed(true)
      .priority(Priority.MEDIUM)
      .tags(Set.of("home"))
      .build();

    TaskResponseDto dto = taskMapper.toResponseDto(task);

    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getTitle()).isEqualTo("Test Task");
    assertThat(dto.getDescription()).isEqualTo("Test Description");
    assertThat(dto.isCompleted()).isTrue();
    assertThat(dto.getPriority()).isEqualTo(Priority.MEDIUM);
    assertThat(dto.getTags()).containsExactly("home");
  }

  @Test
  void updateEntity_ShouldUpdateOnlyNonNullFields() {
    Task task = Task.builder()
      .id(1L)
      .title("Old Title")
      .description("Old Description")
      .completed(false)
      .priority(Priority.LOW)
      .build();

    TaskUpdateDto updateDto = new TaskUpdateDto();
    updateDto.setTitle("New Title");
    updateDto.setPriority(Priority.HIGH);
    // description и completed не передаём

    taskMapper.updateEntity(updateDto, task);

    assertThat(task.getTitle()).isEqualTo("New Title");
    assertThat(task.getDescription()).isEqualTo("Old Description"); // не изменилось
    assertThat(task.isCompleted()).isFalse(); // не изменилось
    assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
  }
}