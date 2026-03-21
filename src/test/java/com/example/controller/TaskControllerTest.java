package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.enums.Priority;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void createTask_ShouldReturn201_WhenValidData() throws Exception {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Test Task");
    dto.setPriority(Priority.HIGH);
    dto.setDueDate(LocalDate.now().plusDays(1));

    mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.title").value("Test Task"))
      .andExpect(jsonPath("$.priority").value("HIGH"))
      .andExpect(jsonPath("$.completed").value(false))
      .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void createTask_ShouldReturn400_WhenTitleIsBlank() throws Exception {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("");
    dto.setPriority(Priority.HIGH);

    mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void createTask_ShouldReturn400_WhenTitleIsTooShort() throws Exception {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("ab");
    dto.setPriority(Priority.HIGH);

    mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void createTask_ShouldReturn400_WhenPriorityIsNull() throws Exception {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Test Task");
    dto.setPriority(null);

    mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void createTask_ShouldReturn400_WhenDueDateIsInPast() throws Exception {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Test Task");
    dto.setPriority(Priority.HIGH);
    dto.setDueDate(LocalDate.of(2020, 1, 1));

    mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getAllTasks_ShouldReturn200_WithTotalCountHeader() throws Exception {
    mockMvc.perform(get("/api/tasks"))
      .andExpect(status().isOk())
      .andExpect(header().exists("X-Total-Count"))
      .andExpect(header().exists("X-API-Version"))
      .andExpect(jsonPath("$").isArray());
  }

  @Test
  void getTaskById_ShouldReturn200_WhenTaskExists() throws Exception {
    // Сначала создаём задачу
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Test Task");
    createDto.setPriority(Priority.HIGH);

    String response = mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    Long id = objectMapper.readTree(response).get("id").asLong();

    // Затем получаем по ID
    mockMvc.perform(get("/api/tasks/{id}", id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(id))
      .andExpect(jsonPath("$.title").value("Test Task"));
  }

  @Test
  void getTaskById_ShouldReturn404_WhenTaskNotFound() throws Exception {
    mockMvc.perform(get("/api/tasks/{id}", 99999L))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Task Not Found"));
  }

  @Test
  void deleteTask_ShouldReturn204_WhenTaskExists() throws Exception {
    // Создаём задачу
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Task to Delete");
    createDto.setPriority(Priority.LOW);

    String response = mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    Long id = objectMapper.readTree(response).get("id").asLong();

    // Удаляем
    mockMvc.perform(delete("/api/tasks/{id}", id))
      .andExpect(status().isNoContent())
      .andExpect(header().exists("X-API-Version"));

    // Проверяем, что удалено
    mockMvc.perform(get("/api/tasks/{id}", id))
      .andExpect(status().isNotFound());
  }
}