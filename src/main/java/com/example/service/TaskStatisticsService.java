package com.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskStatisticsService {

	private final TaskRepository taskRepository;

	@Value("${app.name}")
	private String appName;

	@Value("${app.version}")
	private String appVersion;

	@Value("${server.port}")
	private String serverPort;

	public String getAppInfo() {
		return "App: " + appName + " " + appVersion +  " running on port: " + serverPort;
	}
}
