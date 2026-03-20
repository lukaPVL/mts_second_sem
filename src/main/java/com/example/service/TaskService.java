package com.example.service;

import com.example.model.Task;
import com.example.validation.DueDateNotBeforeCreation;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.repository.TaskRepository;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class TaskService {
	private final TaskRepository taskRepository;

	private final Map<Long, Task> taskCache = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger(TaskService.class);

	@PostConstruct
	public void init() {
		for (Task task : taskRepository.findAll()) {
			taskCache.put(task.getId(), task);
		}
	}

	@PreDestroy
	public void destroy() {
		log.info("Кол-во задач в кэше на момент конца программы (когда TaskService умирает): " + taskCache.size());
	}


	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public List<Task> findAll() {
		return taskRepository.findAll();
	}

	public Task findById(Long id) {
		Optional<Task> taskOpt = taskRepository.findById(id);
		if (taskOpt.isEmpty()) {
			throw new RuntimeException("Not found task with id: " + id);
		}
		return taskOpt.get();
	}
  @DueDateNotBeforeCreation
	public Task save(Task task) {
		return taskRepository.save(task);
	}

  @DueDateNotBeforeCreation
	public Task update(Task task) {
		return taskRepository.update(task);
	}

	public void deleteById(Long id) {
		taskRepository.deleteById(id);
	}
}