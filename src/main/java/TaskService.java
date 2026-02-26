import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
	private final TaskRepository taskRepository;

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

	public Task save(Task task) {
		return taskRepository.save(task);
	}

	public Task update(Task task) {
		return taskRepository.update(task);
	}

	public void deleteById(Long id) {
		taskRepository.deleteById(id);
	}
}