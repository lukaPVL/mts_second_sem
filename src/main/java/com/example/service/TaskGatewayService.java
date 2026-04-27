package com.example.service;

import com.example.client.ExternalTasksClient;
import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskGatewayService {

    private final ExternalTasksClient externalTasksClient;

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public TaskResponseDto createTask(TaskCreateDto dto) {
        return externalTasksClient.createTask(dto);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public TaskResponseDto getTask(Long id) {
        return externalTasksClient.getTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public void deleteTask(Long id) {
        externalTasksClient.deleteTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getAllTasksFallback")
    public List<TaskResponseDto> getAllTasks(Boolean completed, Integer limit) {
        return externalTasksClient.getAllTasks(completed, limit);
    }

    public void deleteTaskFallback(Long id, Throwable t) {
        log.error("Fallback: Не удалось удалить задачу {}. Причина: {}", id, t.getMessage());
    }

    public List<TaskResponseDto> getAllTasksFallback(Boolean completed, Integer limit, Throwable t) {
        log.error("Fallback: Не удалось получить список задач: {}", t.getMessage());
        return List.of();
    }

    public TaskResponseDto createTaskFallback(TaskCreateDto dto, Throwable t) {
        log.error("Fallback: Не удалось создать задачу во внешнем сервисе. Причина: {}", t.getMessage());
        return new TaskResponseDto();
    }

    public TaskResponseDto getTaskFallback(Long id, Throwable t) {
        log.error("Fallback: Ошибка получения задачи {}. Причина: {}", id, t.getMessage());
        return new TaskResponseDto();
    }

}
