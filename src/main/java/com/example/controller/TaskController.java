package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.scope.PrototypeScopedBean;
import com.example.scope.RequestScopedBean;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.service.TaskService;

import java.util.ArrayList;
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

	/**
	 * Получить все задачи
	 */
	@GetMapping
	public List<TaskResponseDto> getAllTasks() {

		System.out.println("REQUEST SCOPED BEAN");
		System.out.println("Request ID: " + requestScopedBean.getRequestId());
		System.out.println("Start time: " + requestScopedBean.getStartTime());

		System.out.println("PROTOTYPE SCOPED BEAN");
		System.out.println("Instance ID: " + prototypeScopedBean.getInstanceId());
		System.out.println("Generated Task ID: " + prototypeScopedBean.generateTaskId());

    List<Task> tasks = taskService.findAll();
		return tasks.stream().map(taskMapper::toResponseDto).toList();
	}

	/**
	 * Получить задачу по ID
	 */
	@GetMapping("/{id}")
	public TaskResponseDto getTaskById(@PathVariable Long id) {
    Task task = taskService.findById(id);
		return taskMapper.toResponseDto(task);
	}

	/**
	 * Создать новую задачу
	 */
	@PostMapping
	public TaskResponseDto createTask(@RequestBody TaskCreateDto createDto) {
    Task task = taskMapper.toEntity(createDto);
    task.setCreatedAtNow();
    Task savedTask = taskService.save(task);
		return taskMapper.toResponseDto(savedTask);
	}

	/**
	 * Обновить существующую задачу
	 */
	@PutMapping("/{id}")
	public TaskResponseDto updateTask(@PathVariable Long id, @RequestBody TaskUpdateDto updateDto) {
		Task existingTask = taskService.findById(id);
    taskMapper.updateEntity(updateDto, existingTask);
    Task updatedTask = taskService.update(existingTask);
		return taskMapper.toResponseDto(updatedTask);
	}

	/**
	 * Удалить задачу по ID
	 */
	@DeleteMapping("/{id}")
	public void deleteTask(@PathVariable Long id) {
		taskService.deleteById(id);
	}
}