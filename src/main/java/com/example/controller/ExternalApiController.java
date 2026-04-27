package com.example.controller;


import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.entity.Task;
import com.example.exception.ExternalApiException;
import com.example.mapper.TaskMapper;
import com.example.service.TaskService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("external/v1/tasks")
@RequiredArgsConstructor
public class ExternalApiController {

        private final TaskService taskService;
        private final TaskMapper taskMapper;

        @PostMapping
        public ResponseEntity<TaskResponseDto> addTask(@RequestBody TaskCreateDto taskCreateDto) {
            Task task = taskMapper.toEntity(taskCreateDto);
            taskService.save(task);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header("Location", "/external/v1/tasks/" + task.getId())
                    .body(taskMapper.toResponseDto(task));
        }

        @DeleteMapping("/{taskId}")
        public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId) {
            taskService.deleteById(taskId);
            return ResponseEntity.noContent().build();
        }

        @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackForUnstable")
        @GetMapping("/unstable")
        public ResponseEntity<?> getUnstable(@RequestParam String mode) throws InterruptedException {
            return switch (mode) {
                case "timeout" -> {
                    Thread.sleep(5000);
                    yield  ResponseEntity.ok().build();
                }
                case "500" -> throw new ExternalApiException("External service error"); //ResponseEntity.status(500).build();
                case "429" -> ResponseEntity.status(429).header("Retry-After", "10").build();
                default -> ResponseEntity.ok("All good");
            };
        }

    public ResponseEntity<?> fallbackForUnstable(String mode, Throwable t) {
        return ResponseEntity.ok("STUB_FOR_UNSTABLE");
    }

        @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackForTask")
        @GetMapping("/{taskId}")
        public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long taskId) {
            Task task = taskService.findById(taskId);
            return ResponseEntity.ok().body(taskMapper.toResponseDto(task));
        }

        @GetMapping
        public ResponseEntity<List<TaskResponseDto>> getAllTasks(
                @RequestParam(required = false) Boolean completed,
                @RequestParam(required = false, defaultValue = "10") Integer limit) {

            List<TaskResponseDto> allTasks = taskService.findAll().stream()
                    .map(taskMapper::toResponseDto)
                    .toList();

            List<TaskResponseDto> filteredTasks = allTasks.stream()
                    .filter(task -> completed == null || task.isCompleted() == completed)
                    .limit(limit)
                    .toList();

            return ResponseEntity.ok(filteredTasks);
        }

    public ResponseEntity<?> fallbackForTask(Long taskId, Throwable t) {
        return ResponseEntity.ok(Map.of("title", "STUB"));
    }
}
