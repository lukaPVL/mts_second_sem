package com.example.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.repository.TaskRepository;

@Service
public class TaskStatisticsService {

	private final TaskRepository taskRepositoryInMemory;
	private final TaskRepository taskRepositoryStub;

	public TaskStatisticsService(TaskRepository taskRepositoryInMemory,
															 @Qualifier("repositoryStub") TaskRepository taskRepositoryStub) {
		this.taskRepositoryInMemory = taskRepositoryInMemory;
		this.taskRepositoryStub = taskRepositoryStub;
	}

	public String compareBeanInfo() {
		return "Primary bean: " + taskRepositoryInMemory + "\n"
			+ "Qualifier bean: " + taskRepositoryStub;
	}
}
