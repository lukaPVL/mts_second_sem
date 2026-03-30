package com.example.entity;

import com.example.enums.Priority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "completed")
  private boolean completed;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "last_update_at")
  private LocalDateTime lastUpdateAt;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "priority")
  @Enumerated(EnumType.STRING)
  private Priority priority;

  @ElementCollection
  @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "task_id"))
  @Column(name = "tag")
  @Builder.Default
  private Set<String> tags = new HashSet<>();

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<TaskAttachment> attachments = new ArrayList<>();

  public void setCreatedAtNow() {
    this.createdAt = LocalDateTime.now();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task task)) return false;
    return id != null && id.equals(task.id);
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}