package com.example.client;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.exception.ExternalApiException;
import com.example.exception.TaskNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.rmi.server.ExportException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalTasksClient {

    private final RestClient restClient;

    public TaskResponseDto getTask(Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/external/v1/tasks/{id}").build(id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    ProblemDetail problemDetail = mapper.readValue(response.getBody(), ProblemDetail.class);
                    String detail = (problemDetail != null) ? problemDetail.getDetail() : "Not found";
                    throw new TaskNotFoundException(detail);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->  {
                    throw new ExternalApiException("External Service error");
                })
                .body(TaskResponseDto.class);
    }

    public TaskResponseDto createTask(TaskCreateDto taskCreateDto) {
        ResponseEntity<TaskResponseDto> taskResponse = restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/external/v1/tasks").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskCreateDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ExternalApiException("Client error during task creating");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->  {
                    throw new ExternalApiException("External service is down");
                })
                .toEntity(TaskResponseDto.class);

        URI location = taskResponse.getHeaders().getLocation();
        if (location != null) {
            log.info("Task created successfully at: {}", location);
        }
        return taskResponse.getBody();
    }

    public void deleteTask(Long id) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/external/v1/tasks/{id}").build(id))
                .retrieve()
                .toBodilessEntity();
    }

}
