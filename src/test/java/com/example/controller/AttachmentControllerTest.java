package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.enums.Priority;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private MockHttpSession session;

  private Long createTestTask() throws Exception {
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Task with Attachment");
    createDto.setPriority(Priority.MEDIUM);
    createDto.setDueDate(LocalDate.now().plusDays(1));

    String response = mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
      .andExpect(status().isCreated())
      .andReturn().getResponse().getContentAsString();

    JsonNode jsonNode = objectMapper.readTree(response);
    Long taskId = jsonNode.get("id").asLong();
    System.out.println("Created task with ID: " + taskId);

    System.out.println("Created task response: " + response);

    return taskId;
  }

  @Test
  void uploadAttachment_ShouldReturn200_WhenValidFile() throws Exception {
    Long taskId = createTestTask();
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "test.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "Hello World".getBytes()
    );

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
        .file(file))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.fileName").value("test.txt"))
      .andExpect(jsonPath("$.size").value(11))
      .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void uploadAttachment_ShouldReturn400_WhenFileIsEmpty() throws Exception {
    Long taskId = createTestTask();

    MockMultipartFile emptyFile = new MockMultipartFile(
      "file",
      "empty.txt",
      MediaType.TEXT_PLAIN_VALUE,
      new byte[0]
    );

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
        .file(emptyFile))
      .andExpect(status().isBadRequest());
  }
  @Test
  void uploadAttachment_ShouldReturn404_WhenTaskNotFound() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "test.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "Hello".getBytes()
    );

    mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 99999L)
        .file(file))
      .andExpect(status().isNotFound());
  }

  @Test
  void getAttachments_ShouldReturn200_WithList() throws Exception {
    Long taskId = createTestTask();
    System.out.println("TASK ID: " + taskId);

    MockMultipartFile file = new MockMultipartFile(
      "file",
      "test.txt",
      MediaType.TEXT_PLAIN_VALUE,
      "Hello".getBytes()
    );

    // Загружаем файл
    MvcResult uploadResult = mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
        .file(file))
      .andReturn();

    System.out.println("Upload status: " + uploadResult.getResponse().getStatus());
    System.out.println("Upload response: " + uploadResult.getResponse().getContentAsString());

    // Получаем список
    MvcResult listResult = mockMvc.perform(get("/api/tasks/{taskId}/attachments", taskId))
      .andReturn();

    System.out.println("List status: " + listResult.getResponse().getStatus());
    System.out.println("List response: " + listResult.getResponse().getContentAsString());

    // Ассерты
    assertThat(listResult.getResponse().getStatus()).isEqualTo(200);
  }
}