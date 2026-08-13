package com.workfluffs.shortsai.domain.project.dto;

import com.workfluffs.shortsai.domain.asset.entity.Asset;
import com.workfluffs.shortsai.domain.asset.enums.AssetStatus;
import com.workfluffs.shortsai.domain.asset.enums.AssetType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AssetResponse {
    private Long id;
    private AssetType type;
    private String fileUrl;
    private Integer cutOrder;
    private Long promptHistoryId;
    private AssetStatus status;
    private LocalDateTime createdAt;

    public static AssetResponse from(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .type(asset.getType())
                .fileUrl(asset.getFileUrl())
                .cutOrder(asset.getCutOrder())
                .promptHistoryId(asset.getPromptUsed() != null ? asset.getPromptUsed().getId() : null)
                .status(asset.getStatus())
                .createdAt(asset.getCreatedAt())
                .build();
    }
}
