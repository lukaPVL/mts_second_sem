package com.example.controller;

import com.example.model.Task;
import com.example.scope.PrototypeScopedBean;
import com.example.scope.RequestScopedBean;
import org.springframework.web.bind.annotation.*;
import com.example.service.TaskService;

import java.util.List;

/**
 * Контроллер для работы с задачами
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
	private final TaskService taskService;
	private final RequestScopedBean requestScopedBean;
	private final PrototypeScopedBean prototypeScopedBean;

	public TaskController(TaskService taskService,
												RequestScopedBean requestScopedBean,
												PrototypeScopedBean prototypeScopedBean) {
		this.taskService = taskService;
		this.requestScopedBean = requestScopedBean;
		this.prototypeScopedBean = prototypeScopedBean;
	}

	/**
	 * Получить все задачи
	 */
	@GetMapping
	public List<Task> getAllTasks() {

		System.out.println("REQUEST SCOPED BEAN");
		System.out.println("Request ID: " + requestScopedBean.getRequestId());
		System.out.println("Start time: " + requestScopedBean.getStartTime());

		System.out.println("PROTOTYPE SCOPED BEAN");
		System.out.println("Instance ID: " + prototypeScopedBean.getInstanceId());
		System.out.println("Generated Task ID: " + prototypeScopedBean.generateTaskId());

		return taskService.findAll();
	}

	/**
	 * Получить задачу по ID
	 */
	@GetMapping("/{id}")
	public Task getTaskById(@PathVariable Long id) {
		return taskService.findById(id);
	}

	/**
	 * Создать новую задачу
	 */
	@PostMapping
	public Task createTask(@RequestBody Task task) {
		return taskService.save(task);
	}

	/**
	 * Обновить существующую задачу
	 */
	@PutMapping("/{id}")
	public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
		task.setId(id);
		return taskService.update(task);
	}

	/**
	 * Удалить задачу по ID
	 */
	@DeleteMapping("/{id}")
	public void deleteTask(@PathVariable Long id) {
		taskService.deleteById(id);
	}
}