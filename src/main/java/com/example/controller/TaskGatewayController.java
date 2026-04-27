package com.example.controller;


import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.service.TaskGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskGatewayController {

    private final TaskGatewayService taskGatewayService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskCreateDto taskCreateDto) {
        TaskResponseDto response = taskGatewayService.createTask(taskCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskGatewayService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(taskGatewayService.getAllTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskGatewayService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
