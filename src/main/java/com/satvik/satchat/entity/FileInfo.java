package com.satvik.satchat.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "file", schema = "public", catalog = "postgres")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class FileInfo {
  @Id
  @Column(name = "id", nullable = false, columnDefinition = "uuid")
  private UUID id;

  private String name;
  private String url;

  @Column(name = "time")
  @CreatedDate
  private Timestamp time;

  @Column(name = "from_user", columnDefinition = "uuid")
  private UUID fromUser;

  public FileInfo(String name, String url) {
    this.name = name;
    this.url = url;
  }
}
