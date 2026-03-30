package com.example.controller;

import com.example.dto.TaskResponseDto;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.service.FavoritesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoritesController {

  private final FavoritesService favoritesService;
  private final TaskMapper taskMapper;

  @Value("${app.api.version:2.0.0}")
  private String apiVersion;

  @PostMapping("/{taskId}")
  public ResponseEntity<Void> addToFavorites(@PathVariable Long taskId, HttpSession session) {
    favoritesService.addToFavorite(taskId, session);
    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .build();
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> removeFromFavorites(@PathVariable Long taskId, HttpSession session) {
    favoritesService.removeFromFavorite(taskId, session);
    return ResponseEntity.noContent()
      .header("X-API-Version", apiVersion)
      .build();
  }

  @GetMapping
  public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
    List<Task> tasks = favoritesService.getFavoriteTasks(session);

    List<TaskResponseDto> responseDtoList = tasks.stream()
        .map(taskMapper::toResponseDto)
        .toList();

    return ResponseEntity.ok()
      .header("X-API-Version", apiVersion)
      .body(responseDtoList);
  }
}
