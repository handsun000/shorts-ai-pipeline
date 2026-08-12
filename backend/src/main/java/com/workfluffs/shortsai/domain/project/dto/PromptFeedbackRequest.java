package com.workfluffs.shortsai.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromptFeedbackRequest {
    @NotBlank(message = "Feedback cannot be empty")
    private String feedback;
}
