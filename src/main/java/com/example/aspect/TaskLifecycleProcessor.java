package com.example.aspect;

import com.example.repository.TaskRepository;
import com.example.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Процессор для логирования жизненного цикла бинов
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

	private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof TaskService || bean instanceof TaskRepository) {
			log.info("До инициализации бина: {} (класс: {})",
				beanName, bean.getClass().getSimpleName());
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof TaskService || bean instanceof TaskRepository) {
			log.info("После инициализации бина: {} (класс: {})",
				beanName, bean.getClass().getSimpleName());
		}
		return bean;
	}
}