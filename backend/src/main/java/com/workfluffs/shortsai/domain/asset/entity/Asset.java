package com.workfluffs.shortsai.domain.asset.entity;

import com.workfluffs.shortsai.domain.asset.enums.AssetStatus;
import com.workfluffs.shortsai.domain.asset.enums.AssetType;
import com.workfluffs.shortsai.domain.project.entity.Project;
import com.workfluffs.shortsai.domain.project.entity.PromptHistory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType type;

    private String fileUrl; // 로컬 경로 또는 스토리지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_history_id")
    private PromptHistory promptUsed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Asset(Project project, AssetType type, String fileUrl, PromptHistory promptUsed, AssetStatus status) {
        this.project = project;
        this.type = type;
        this.fileUrl = fileUrl;
        this.promptUsed = promptUsed;
        this.status = status != null ? status : AssetStatus.PENDING;
    }

    public void updateStatus(AssetStatus status) {
        this.status = status;
    }

    public void updateFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
