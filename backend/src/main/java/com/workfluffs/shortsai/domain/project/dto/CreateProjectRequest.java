package com.workfluffs.shortsai.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateProjectRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Selected script idea description is required")
    private String description;
}
