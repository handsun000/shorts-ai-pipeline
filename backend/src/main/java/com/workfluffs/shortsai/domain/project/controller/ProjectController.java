package com.workfluffs.shortsai.domain.project.controller;

import com.workfluffs.shortsai.common.dto.ApiResponse;
import com.workfluffs.shortsai.domain.project.dto.*;
import com.workfluffs.shortsai.domain.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/ideas")
    public ApiResponse<String> generateIdeas(@Valid @RequestBody IdeaRequest request) {
        String ideas = projectService.generateIdeas(request.getTopic());
        return ApiResponse.success(ideas);
    }

    @PostMapping
    public ApiResponse<ProjectDetailResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectDetailResponse response = projectService.createProject(request);
        return ApiResponse.success("Project created successfully", response);
    }

    @GetMapping
    public ApiResponse<List<ProjectDetailResponse>> getAllProjects() {
        List<ProjectDetailResponse> projects = projectService.getAllProjects();
        return ApiResponse.success(projects);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectDetailResponse> getProjectDetail(@PathVariable Long id) {
        ProjectDetailResponse project = projectService.getProjectDetail(id);
        return ApiResponse.success(project);
    }

    @PostMapping("/{id}/image-prompt")
    public ApiResponse<PromptHistoryResponse> generateImagePrompt(@PathVariable Long id) {
        PromptHistoryResponse response = projectService.generateImagePrompt(id);
        return ApiResponse.success("Image prompt generated", response);
    }

    @PostMapping("/{id}/image-prompt/feedback")
    public ApiResponse<PromptHistoryResponse> handleImagePromptFeedback(
            @PathVariable Long id,
            @Valid @RequestBody PromptFeedbackRequest request) {
        PromptHistoryResponse response = projectService.handleImagePromptFeedback(id, request.getFeedback());
        return ApiResponse.success("Image prompt regenerated with feedback", response);
    }

    @PostMapping("/{id}/approve-image/{promptId}")
    public ApiResponse<ProjectDetailResponse> approveImagePrompt(
            @PathVariable Long id,
            @PathVariable Long promptId) {
        ProjectDetailResponse response = projectService.approveImagePrompt(id, promptId);
        return ApiResponse.success("Image prompt approved", response);
    }

    @PostMapping("/{id}/video-prompt")
    public ApiResponse<PromptHistoryResponse> generateVideoPrompt(@PathVariable Long id) {
        PromptHistoryResponse response = projectService.generateVideoPrompt(id);
        return ApiResponse.success("Video prompt generated", response);
    }

    @PostMapping("/{id}/generate-video")
    public ApiResponse<String> triggerVideoGeneration(@PathVariable Long id) {
        projectService.triggerVideoGeneration(id);
        return ApiResponse.success("Video generation triggered. Subscribe to SSE endpoint for status updates.");
    }
}
