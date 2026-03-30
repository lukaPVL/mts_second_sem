package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "task_attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "stored_file_name")
  private String storedFileName;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "size")
  private Long size;

  @CreatedDate
  @Column(name = "uploaded_at")
  private LocalDateTime uploadedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TaskAttachment that)) return false;
    return id != null && id.equals(that.id);
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}