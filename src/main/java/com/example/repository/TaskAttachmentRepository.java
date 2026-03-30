package com.example.repository;

import com.example.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

  TaskAttachment save(TaskAttachment attachment);

  Optional<TaskAttachment> findById(Long id);

  List<TaskAttachment> findByTask_Id(Long taskId);

  void deleteById(Long id);

  boolean existsById(Long id);
}
