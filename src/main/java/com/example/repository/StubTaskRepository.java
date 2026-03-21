package com.example.repository;

import com.example.enums.Priority;
import com.example.exception.TaskNotFoundException;
import com.example.model.Task;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StubTaskRepository implements TaskRepository {

  private final Map<Long, Task> stubTasks = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(stubTasks.values());
  }

  @Override
  public Optional<Task> findById(Long id) {
    return Optional.ofNullable(stubTasks.get(id));
  }

  @Override
  public Task save(Task task) {
    if (task.getId() == null || task.getId() == 0) {
      task.setId(idGenerator.getAndIncrement());
    }
    stubTasks.put(task.getId(), task);
    return task;
  }

  @Override
  public Task update(Task task) {
    if (task.getId() == null) {
      throw new IllegalArgumentException("Task id cannot be null");
    }
    if (!stubTasks.containsKey(task.getId())) {
      throw new TaskNotFoundException("Task with id " + task.getId() + " not found");
    }
    stubTasks.put(task.getId(), task);
    return task;
  }

  @Override
  public void deleteById(Long id) {
    stubTasks.remove(id);
  }

  @Override
  public void initialize() {
    if (stubTasks.isEmpty()) {
      Task task1 = Task.builder()
        .title("Task 1")
        .description("Task description 1")
        .completed(false)
        .priority(Priority.MEDIUM)
        .tags(new HashSet<>())
        .build();
      task1.setCreatedAtNow();
      save(task1);

      Task task2 = Task.builder()
        .title("Task 2")
        .description("Task description 2")
        .completed(true)
        .priority(Priority.HIGH)
        .tags(new HashSet<>())
        .build();
      task2.setCreatedAtNow();
      save(task2);
    }
  }
}