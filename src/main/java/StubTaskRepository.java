import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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
		if (task.getId() == 0) {
			task.setId(idGenerator.getAndIncrement());
		}
		stubTasks.put(task.getId(), task);
		return task;
	}

	@Override
	public Task update(Task task) {
		if (task.getId() != 0 && stubTasks.containsKey(task.getId())) {
			stubTasks.put(task.getId(), task);
			return task;
		}
		throw new IllegalArgumentException("Not found Task with id: " + task.getId());
	}

	@Override
	public void deleteById(Long id) {
		stubTasks.remove(id);
	}

	@Override
	public void initialize() {
		save(new Task(0, "Task 1", "Task description 1", false));
		save(new Task(0, "Task 2", "Task description 2", true));
	}
}