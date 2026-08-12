package com.workfluffs.shortsai.domain.project.dto;

import com.workfluffs.shortsai.domain.project.entity.Project;
import com.workfluffs.shortsai.domain.project.enums.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProjectDetailResponse {
    private Long id;
    private String title;
    private String description;
    private ProjectStatus status;
    private List<PromptHistoryResponse> promptHistories;
    private List<AssetResponse> assets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectDetailResponse of(Project project, List<PromptHistoryResponse> prompts, List<AssetResponse> assets) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus())
                .promptHistories(prompts)
                .assets(assets)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
