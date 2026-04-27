package com.example.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Бин с областью видимости prototype (новый при каждом обращении)
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {
	private final String instanceId;

	public PrototypeScopedBean() {
		this.instanceId = UUID.randomUUID().toString();
		System.out.println("СОЗДАН НОВЫЙ PrototypeScopedBean: " + instanceId);
	}

	/**
	 * Сгенерировать новый ID для задачи
	 */
	public String generateTaskId() {
		return "TASK: " + UUID.randomUUID().toString().substring(0, 8);
	}

	/**
	 * Получить ID экземпляра бина
	 */
	public String getInstanceId() {
		return instanceId;
	}
}