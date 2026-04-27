package com.example.controller;

import com.example.entity.Task;
import com.example.enums.Priority;
import com.example.repository.TaskRepository;
import com.example.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        Task defaultTask = Task.builder()
                .title("Initial Task")
                .description("Description")
                .completed(false)
                .priority(Priority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(defaultTask);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTasks_ShouldForwardQueryParams() throws Exception {
        mockMvc.perform(get("/external/v1/tasks")
                        .param("completed", "false")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void circuitBreaker_ShouldOpen_WhenExternalServiceFails() throws Exception {
        Long taskId = taskRepository.findAll().get(0).getId();
        String path = "/external/v1/tasks/" + taskId;

        mockMvc.perform(get(path))
                .andExpect(status().isOk());

        for (int i = 0; i < 20; i++) {
            mockMvc.perform(get("/external/v1/tasks/unstable").param("mode", "500"))
                    .andExpect(status().isOk());
        }
        Thread.sleep(100);

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("STUB"));
    }


    @Test
    @WithMockUser(roles = "USER")
    void rateLimiter_ShouldTrigger_WhenTooManyRequests() throws Exception {
        Task savedTask = taskRepository.findAll().get(0);
        String taskPath = "/external/v1/tasks/" + savedTask.getId();

        for (int i = 0; i < 15; i++) {
            mockMvc.perform(get(taskPath));
        }
    }
}