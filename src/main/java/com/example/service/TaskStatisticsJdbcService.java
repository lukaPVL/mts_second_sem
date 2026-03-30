package com.example.service;

import com.example.dto.PriorityStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatisticsJdbcService {

  private final JdbcTemplate jdbcTemplate;

  public List<PriorityStatDto> getTasksCountByPriority() {
    String sql = "SELECT priority, COUNT(*) as cnt FROM tasks GROUP BY priority";

    return jdbcTemplate.query(sql, (rs, rowNum) -> new PriorityStatDto(
            rs.getString("priority"),
            rs.getLong("cnt")
    ));
  }
}
