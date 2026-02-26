package config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import repository.StubTaskRepository;
import repository.TaskRepository;

@Configuration
public class RepositoryConfig {

	@Bean
	@Qualifier("repositoryStub")
	public TaskRepository stubRepository() {
		StubTaskRepository repository = new StubTaskRepository();
		repository.initialize();
		return repository;
	}
}