package com.satvik.satchat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "conversation", schema = "public", catalog = "postgres")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ConversationEntity {
  @Id
  @Column(name = "id", nullable = false, columnDefinition = "uuid")
  private UUID id;

  @Column(name = "conv_id", length = -1)
  private String convId;

  @Column(name = "from_user", columnDefinition = "uuid")
  private UUID fromUser;

  @Column(name = "to_user", columnDefinition = "uuid")
  private UUID toUser;

  @Column(name = "time")
  @CreatedDate
  private Timestamp time;

  @Column(name = "last_modified")
  @LastModifiedDate
  private Timestamp lastModified;

  @Column(name = "content", length = -1)
  private String content;

  @Column(name = "delivery_status", length = -1)
  private String deliveryStatus;

  @Column(name = "file_id", length = -1)
  private String fileId;
}
