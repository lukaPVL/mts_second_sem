package com.example.service;

import com.example.dto.TaskCreateDto;
import com.example.enums.Priority;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FavoritesServiceTest {

  @Autowired
  private FavoritesService favoritesService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskMapper taskMapper;  // ← добавляем маппер

  private HttpSession session;
  private Long taskId;

  @BeforeEach
  void setUp() {
    session = new MockHttpSession();

    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Test Task");
    createDto.setPriority(Priority.MEDIUM);

    Task task = taskMapper.toEntity(createDto);
    task.setCreatedAtNow();
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
  void addToFavorite_ShouldNotAddDuplicate() {
    favoritesService.addToFavorite(taskId, session);
    favoritesService.addToFavorite(taskId, session);

    List<Task> favorites = favoritesService.getFavoriteTasks(session);
    assertThat(favorites).hasSize(1);
  }

  @Test
  void removeFromFavorite_ShouldRemoveTaskFromFavorites() {
    favoritesService.addToFavorite(taskId, session);
    favoritesService.removeFromFavorite(taskId, session);

    List<Task> favorites = favoritesService.getFavoriteTasks(session);
    assertThat(favorites).isEmpty();
  }

  @Test
  void removeFromFavorite_ShouldNotThrow_WhenTaskNotInFavorites() {
    favoritesService.removeFromFavorite(taskId, session);

    List<Task> favorites = favoritesService.getFavoriteTasks(session);
    assertThat(favorites).isEmpty();
  }

  @Test
  void getFavoriteTasks_ShouldReturnEmptyList_WhenNoFavorites() {
    List<Task> favorites = favoritesService.getFavoriteTasks(session);
    assertThat(favorites).isEmpty();
  }
}