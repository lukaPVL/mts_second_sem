package com.example.repository;

import com.example.entity.Task;
import com.example.entity.TaskAttachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Test
  void findUpcomingTask_ShouldReturnTasksWithinSevenDays() {
    Task upcoming = Task.builder().title("Soon").dueDate(LocalDate.now().plusDays(3)).build();
    Task farAway = Task.builder().title("Far").dueDate(LocalDate.now().plusDays(10)).build();
    taskRepository.saveAll(List.of(upcoming, farAway));

    List<Task> results = taskRepository.findUpcomingTask(LocalDate.now().plusDays(7));

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getTitle()).isEqualTo("Soon");
  }

  @Test
  void findAll_ShouldLoadAttachmentsWithoutNPlusOne() {
    Task task = taskRepository.save(Task.builder().title("Task with files").build());
    TaskAttachment attachment = TaskAttachment.builder()
      .fileName("test.pdf")
      .task(task)
      .build();

    List<Task> tasks = taskRepository.findAll();

    assertThat(tasks).isNotEmpty();
  }
}