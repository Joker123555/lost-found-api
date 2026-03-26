package com.campus.lostfound.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer type;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 128)
    private String location;

    @Column(name = "happened_at", nullable = false)
    private LocalDateTime happenedAt;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "reject_reason", length = 512)
    private String rejectReason;

    @Column(name = "contact_name", nullable = false, length = 32)
    private String contactName;

    @Column(name = "contact_phone", nullable = false, length = 32)
    private String contactPhone;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(name = "is_deleted", nullable = false)
    private Integer isDeleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 列表接口临时填充：首张图 URL，不入库 */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String coverUrl;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (viewCount == null) viewCount = 0;
        if (version == null) version = 0;
        if (isDeleted == null) isDeleted = 0;
        if (status == null) status = 0;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
