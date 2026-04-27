package com.example.controller;


import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.entity.Task;
import com.example.mapper.TaskMapper;
import com.example.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/unstable")
    public ResponseEntity<?> getUnstable(@RequestParam String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(5000);
                yield  ResponseEntity.ok().build();
            }
            case "500" -> ResponseEntity.status(500).build();
            case "429" -> ResponseEntity.status(429).header("Retry-After", "10").build();
            default -> ResponseEntity.ok("All good");
        };
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long taskId) {
        Task task = taskService.findById(taskId);
        return ResponseEntity.ok().body(taskMapper.toResponseDto(task));
    }
}
