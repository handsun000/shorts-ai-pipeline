package com.workfluffs.shortsai.domain.project.entity;

import com.workfluffs.shortsai.domain.project.enums.PromptType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "prompt_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PromptHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromptType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String negativeContent;

    @Column(nullable = false)
    private Integer cutOrder;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Boolean isApproved;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PromptHistory(Project project, PromptType type, String content, String negativeContent, Integer cutOrder, Integer version, Boolean isApproved) {
        this.project = project;
        this.type = type;
        this.content = content;
        this.negativeContent = negativeContent;
        this.cutOrder = cutOrder != null ? cutOrder : 1;
        this.version = version != null ? version : 1;
        this.isApproved = isApproved != null ? isApproved : false;
    }

    public void approve() {
        this.isApproved = true;
    }
}
