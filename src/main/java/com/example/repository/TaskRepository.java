package com.example.repository;

import com.example.enums.Priority;
import com.example.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;



@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

  @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN CURRENT_DATE AND :sevenDaysLater")
  List<Task> findUpcomingTask(LocalDate sevenDaysLater);

  @EntityGraph(attributePaths = {"attachments", "tags"})
  List<Task> findAll();
}
