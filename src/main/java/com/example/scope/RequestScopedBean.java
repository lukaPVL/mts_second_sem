package com.example.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Бин с областью видимости request (новый для каждого запроса)
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {
	private final String requestId;
	private final LocalDateTime startTime;

	public RequestScopedBean() {
		this.requestId = UUID.randomUUID().toString();
		this.startTime = LocalDateTime.now();
		System.out.println("СОЗДАН НОВЫЙ RequestScopedBean: " + requestId);
	}

	/**
	 * Получить ID запроса
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Получить время начала запроса
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * Получить время обработки запроса
	 */
	public long getProcessingTime() {
		return java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
	}
}