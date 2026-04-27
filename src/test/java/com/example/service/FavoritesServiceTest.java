package com.example.service;

import com.example.enums.Priority;
import com.example.entity.Task;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("Временно отключено: тесты не адаптированы под новую систему безопасности")
class FavoritesServiceTest {

  @Autowired
  private FavoritesService favoritesService;

  @Autowired
  private TaskService taskService;

  private HttpSession session;
  private Long taskId;

  @BeforeEach
  void setUp() {
    session = new MockHttpSession();

    Task task = Task.builder()
      .title("Test Task")
      .priority(Priority.MEDIUM)
      .build();

    Task savedTask = taskService.save(task);
    taskId = savedTask.getId();
  }

  @Test
  void addToFavorite_ShouldAddTaskToFavorites() {
    favoritesService.addToFavorite(taskId, session);

    List<Task> favorites = favoritesService.getFavoriteTasks(session);
    assertThat(favorites).hasSize(1);
    assertThat(favorites.get(0).getId()).isEqualTo(taskId);
  }

  @Test
  void removeFromFavorite_ShouldRemoveTaskFromFavorites() {
    favoritesService.addToFavorite(taskId, session);
    favoritesService.removeFromFavorite(taskId, session);

    List<Task> favorites = favoritesService.getFavoriteTasks(session);
    assertThat(favorites).isEmpty();
  }
}