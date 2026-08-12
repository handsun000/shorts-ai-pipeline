package com.workfluffs.shortsai.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IdeaRequest {
    @NotBlank(message = "Topic or prompt cannot be empty")
    private String topic;
}
