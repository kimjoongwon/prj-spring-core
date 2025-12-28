package org.plate.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 기본 엔티티 (공통 필드)
 * prj-core의 Prisma 스키마와 동일한 구조
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "seq", unique = true, insertable = false, updatable = false)
    private Integer seq;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    /**
     * 소프트 삭제
     */
    public void softDelete() {
        this.removedAt = LocalDateTime.now();
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isRemoved() {
        return this.removedAt != null;
    }
}
