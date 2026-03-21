package com.example.controller;

import com.example.dto.*;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.scope.PrototypeScopedBean;
import com.example.scope.RequestScopedBean;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.service.TaskService;

import java.util.List;

/**
 * Контроллер для работы с задачами
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
	private final TaskService taskService;
  private final TaskMapper taskMapper;
	private final RequestScopedBean requestScopedBean;
	private final PrototypeScopedBean prototypeScopedBean;

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

	/**
	 * Получить все задачи
	 */
	@GetMapping
	public ResponseEntity<List<TaskResponseDto>> getAllTasks() {

		System.out.println("REQUEST SCOPED BEAN");
		System.out.println("Request ID: " + requestScopedBean.getRequestId());
		System.out.println("Start time: " + requestScopedBean.getStartTime());

		System.out.println("PROTOTYPE SCOPED BEAN");
		System.out.println("Instance ID: " + prototypeScopedBean.getInstanceId());
		System.out.println("Generated Task ID: " + prototypeScopedBean.generateTaskId());

    List<Task> tasks = taskService.findAll();
    List<TaskResponseDto> responseDtoList = tasks.stream().map(taskMapper::toResponseDto).toList();
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(responseDtoList.size()))
      .header("X-API-Version", apiVersion)
      .body(responseDtoList);
	}

	/**
	 * Получить задачу по ID
	 */
	@GetMapping("/{id}")
	public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
    Task task = taskService.findById(id);
    TaskResponseDto responseDto = taskMapper.toResponseDto(task);
    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .body(responseDto);
	}

	/**
	 * Создать новую задачу
	 */
	@PostMapping
	public ResponseEntity<TaskResponseDto> createTask(
      @Validated(OnCreate.class) @RequestBody TaskCreateDto createDto) {
    Task task = taskMapper.toEntity(createDto);
    task.setCreatedAtNow();
    Task savedTask = taskService.save(task);

    TaskResponseDto responseDto = taskMapper.toResponseDto(savedTask);
    return ResponseEntity.status(HttpStatus.CREATED)
      .header("X-API-Version", apiVersion)
      .body(responseDto);
	}

	/**
	 * Обновить существующую задачу
	 */
	@PutMapping("/{id}")
	public ResponseEntity<TaskResponseDto> updateTask(
    @PathVariable Long id,
    @Validated(OnUpdate.class) @RequestBody TaskUpdateDto updateDto) {
		Task existingTask = taskService.findById(id);
    taskMapper.updateEntity(updateDto, existingTask);
    Task updatedTask = taskService.update(existingTask);

    TaskResponseDto responseDto = taskMapper.toResponseDto(updatedTask);
    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .body(responseDto);
	}

	/**
	 * Удалить задачу по ID
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		taskService.deleteById(id);
    return ResponseEntity.noContent()
      .header("X-API-Version", apiVersion)
      .build();
	}
}