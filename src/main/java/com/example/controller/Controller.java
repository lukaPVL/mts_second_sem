package com.example.controller;

import com.example.model.Task;
import org.springframework.web.bind.annotation.*;
import com.example.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class Controller {
	private final TaskService taskService;

	public Controller(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping
	public List<Task> getAllTasks() {
		return taskService.findAll();
	}

	@GetMapping("/{id}")
	public Task getTaskById(@PathVariable Long id) {
		return taskService.findById(id);
	}

	@PostMapping
	public Task createTask(@RequestBody Task task) {
		return taskService.save(task);
	}

	@PutMapping("/{id}")
	public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
		task.setId(id);
		return taskService.update(task);
	}

	@DeleteMapping("/{id}")
	public void deleteTask(@PathVariable Long id) {
		taskService.deleteById(id);
	}
}