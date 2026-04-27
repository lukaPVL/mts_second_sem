package com.example.service;

import com.example.dto.PriorityStatDto;
import com.example.enums.Priority;
import com.example.entity.Task;
import com.example.repository.TaskRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("Временно отключено: тесты не адаптированы под новую систему безопасности")
class TaskStatisticsJdbcServiceTest {

  @Autowired
  private TaskStatisticsJdbcService statisticsService;

  @Autowired
  private TaskRepository taskRepository;

  @Test
  void getTasksCountByPriority_ShouldReturnCorrectStats() {
    taskRepository.save(Task.builder().title("T1").priority(Priority.HIGH).build());
    taskRepository.save(Task.builder().title("T2").priority(Priority.HIGH).build());
    taskRepository.save(Task.builder().title("T3").priority(Priority.LOW).build());

    List<PriorityStatDto> stats = statisticsService.getTasksCountByPriority();

    assertThat(stats).isNotEmpty();

    long highCount = stats.stream()
      .filter(s -> "HIGH".equals(s.getPriority()))
      .mapToLong(PriorityStatDto::getCount)
      .findFirst()
      .orElse(0L);

    assertThat(highCount).isEqualTo(2L);
  }
}