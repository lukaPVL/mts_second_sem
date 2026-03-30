package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.enums.Priority;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FavoritesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private MockHttpSession session;

  @BeforeEach
  void setUp() {
    session = new MockHttpSession();  // ← создаём сессию
  }

  private Long createTestTask() throws Exception {
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Favorite Task");
    createDto.setPriority(Priority.HIGH);
    createDto.setDueDate(LocalDate.now().plusDays(1));

    String response = mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto))
        .session(session))
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    return objectMapper.readTree(response).get("id").asLong();
  }

  @Test
  void addToFavorites_ShouldReturn200_WhenTaskExists() throws Exception {
    Long taskId = createTestTask();

    mockMvc.perform(post("/api/favorites/{taskId}", taskId))
      .andExpect(status().isOk())
      .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void addToFavorites_ShouldReturn404_WhenTaskNotFound() throws Exception {
    mockMvc.perform(post("/api/favorites/{taskId}", 99999L))
      .andExpect(status().isNotFound());
  }

  @Test
  void getFavorites_ShouldReturn200_WithList() throws Exception {
    Long taskId = createTestTask();

    // Добавляем в избранное
    mockMvc.perform(post("/api/favorites/{taskId}", taskId)
        .session(session))
      .andExpect(status().isOk());

    // Получаем список избранного
    mockMvc.perform(get("/api/favorites")
        .session(session))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(taskId))
      .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void removeFromFavorites_ShouldReturn204_WhenTaskInFavorites() throws Exception {
    Long taskId = createTestTask();

    // Добавляем в избранное
    mockMvc.perform(post("/api/favorites/{taskId}", taskId))
      .andExpect(status().isOk());

    // Удаляем из избранного
    mockMvc.perform(delete("/api/favorites/{taskId}", taskId))
      .andExpect(status().isNoContent())
      .andExpect(header().exists("X-API-Version"));

    // Проверяем, что избранное пусто
    mockMvc.perform(get("/api/favorites"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isEmpty());
  }
}