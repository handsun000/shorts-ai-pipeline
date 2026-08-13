package com.workfluffs.shortsai.domain.project.dto;

import com.workfluffs.shortsai.domain.project.entity.PromptHistory;
import com.workfluffs.shortsai.domain.project.enums.PromptType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PromptHistoryResponse {
    private Long id;
    private PromptType type;
    private String content;
    private String negativeContent;
    private Integer cutOrder;
    private Integer version;
    private Boolean isApproved;
    private LocalDateTime createdAt;

    public static PromptHistoryResponse from(PromptHistory prompt) {
        return PromptHistoryResponse.builder()
                .id(prompt.getId())
                .type(prompt.getType())
                .content(prompt.getContent())
                .negativeContent(prompt.getNegativeContent())
                .cutOrder(prompt.getCutOrder())
                .version(prompt.getVersion())
                .isApproved(prompt.getIsApproved())
                .createdAt(prompt.getCreatedAt())
                .build();
    }
}
