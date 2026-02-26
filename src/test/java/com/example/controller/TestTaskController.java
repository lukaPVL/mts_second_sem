package com.example.controller;

import com.example.model.Task;
import com.example.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestTaskController {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private TaskService taskService;

	// GET /api/tasks (получить все задачи)

	@Test
	void getAllTasksShouldReturnList() {
		Task task1 = new Task(1L, "Задача 1", "Описание 1", false);
		Task task2 = new Task(2L, "Задача 2", "Описание 2", true);
		when(taskService.findAll()).thenReturn(Arrays.asList(task1, task2));

		ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().length).isEqualTo(2);
	}

	@Test
	void getAllTasksShouldReturnEmptyList() {
		when(taskService.findAll()).thenReturn(Arrays.asList());

		ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().length).isEqualTo(0);
	}

	// GET /api/tasks/{id} (получить одну задачу)

	@Test
	void getTaskByIdShouldReturnTask() {
		Long id = 1L;
		Task task = new Task(id, "Тест", "Описание", false);
		when(taskService.findById(id)).thenReturn(task);

		ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/{id}", Task.class, id);

		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(id);
		assertThat(response.getBody().getTitle()).isEqualTo("Тест");
	}

	@Test
	void getTaskByIdShouldReturnError_WhenTaskNotFound() {
		Long id = 999L;
		when(taskService.findById(id)).thenThrow(new RuntimeException("Задача не найдена"));

		ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks/{id}", String.class, id);

		assertThat(response.getStatusCode().value()).isEqualTo(500);
	}

	// POST /api/tasks (создать задачу)

	@Test
	void createTaskShouldCreateNewTask() {
		Task newTask = new Task(0L, "Новая", "Описание", false);
		Task createdTask = new Task(1L, "Новая", "Описание", false);
		when(taskService.save(any(Task.class))).thenReturn(createdTask);

		ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", newTask, Task.class);

		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isEqualTo(1L);
	}

	@Test
	void createTaskShouldReturnError_WhenTaskIsNull() {
		when(taskService.save(null)).thenThrow(new RuntimeException("Задача не может быть пустой"));

		ResponseEntity<String> response = restTemplate.postForEntity("/api/tasks", null, String.class);

		assertThat(response.getStatusCode().value()).isEqualTo(415);
	}

	// PUT /api/tasks/{id} (обновить задачу)

	@Test
	void updateTaskShouldUpdateTask() {
		Long id = 1L;
		Task updatedTask = new Task(id, "Обновлено", "Новое описание", true);
		when(taskService.update(any(Task.class))).thenReturn(updatedTask);

		restTemplate.put("/api/tasks/{id}", updatedTask, id);

		when(taskService.findById(id)).thenReturn(updatedTask);
		ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/{id}", Task.class, id);

		assertThat(response.getBody().getTitle()).isEqualTo("Обновлено");
	}

	@Test
	void updateTaskShouldReturnErrorWhenTaskNotFound() {
		Long id = 999L;
		Task task = new Task(id, "Не важно", "Описание", false);
		when(taskService.update(any(Task.class))).thenThrow(new RuntimeException("Задача не найдена"));

		restTemplate.put("/api/tasks/{id}", task, id);

		when(taskService.findById(id)).thenThrow(new RuntimeException("Задача не найдена"));
		ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks/{id}", String.class, id);

		assertThat(response.getStatusCode().value()).isEqualTo(500);
	}

	// DELETE /api/tasks/{id} (удалить задачу)

	@Test
	void deleteTaskShouldDeleteTask() {
		Long id = 1L;
		doNothing().when(taskService).deleteById(id);

		restTemplate.delete("/api/tasks/{id}", id);

		verify(taskService, times(1)).deleteById(id);
	}

	@Test
	void deleteTaskShouldReturnError_WhenTaskNotFound() {
		Long id = 999L;
		doThrow(new RuntimeException("Задача не найдена")).when(taskService).deleteById(id);

		restTemplate.delete("/api/tasks/{id}", id);

		verify(taskService, times(1)).deleteById(id);
	}
}