package com.example.repository;

import com.example.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {
	private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
	private final AtomicLong idGenerator = new AtomicLong(1);

	@Override
	public List<Task> findAll() {
		return new ArrayList<>(tasks.values());
	}

	@Override
	public Optional<Task> findById(Long id) {
		return Optional.ofNullable(tasks.get(id));
	}

	@Override
	public Task save(Task task) {
		if (task.getId() == 0) {
			task.setId(idGenerator.getAndIncrement());
		}
		tasks.put(task.getId(), task);
		return task;
	}

	@Override
	public Task update(Task task) {
		if (task.getId() != 0 && tasks.containsKey(task.getId())) {
			tasks.put(task.getId(), task);
			return task;
		}
		throw new IllegalArgumentException("Not found model.Task with id: " + task.getId());
	}

	@Override
	public void deleteById(Long id) {
		tasks.remove(id);
	}

	@Override
	public void initialize() {}
}