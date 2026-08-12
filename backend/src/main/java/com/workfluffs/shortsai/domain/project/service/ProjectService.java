package com.workfluffs.shortsai.domain.project.service;

import com.workfluffs.shortsai.domain.asset.repository.AssetRepository;
import com.workfluffs.shortsai.domain.project.dto.*;
import com.workfluffs.shortsai.domain.project.entity.Project;
import com.workfluffs.shortsai.domain.project.entity.PromptHistory;
import com.workfluffs.shortsai.domain.project.enums.ProjectStatus;
import com.workfluffs.shortsai.domain.project.enums.PromptType;
import com.workfluffs.shortsai.domain.project.repository.ProjectRepository;
import com.workfluffs.shortsai.domain.project.repository.PromptHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PromptHistoryRepository promptHistoryRepository;
    private final AssetRepository assetRepository;
    private final Agent1Service agent1Service;
    private final Agent2Service agent2Service;
    private final VideoGenerationService videoGenerationService;

    public String generateIdeas(String topic) {
        return agent1Service.generateIdea(topic);
    }

    @Transactional
    public ProjectDetailResponse createProject(CreateProjectRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(ProjectStatus.IDEA_GENERATED)
                .build();
        Project saved = projectRepository.save(project);
        return getProjectDetail(saved.getId());
    }

    public List<ProjectDetailResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(p -> getProjectDetail(p.getId()))
                .collect(Collectors.toList());
    }

    public ProjectDetailResponse getProjectDetail(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        List<PromptHistoryResponse> prompts = promptHistoryRepository.findByProjectIdOrderByVersionDesc(id).stream()
                .map(PromptHistoryResponse::from)
                .collect(Collectors.toList());

        List<AssetResponse> assets = assetRepository.findByProjectId(id).stream()
                .map(AssetResponse::from)
                .collect(Collectors.toList());

        return ProjectDetailResponse.of(project, prompts, assets);
    }

    @Transactional
    public PromptHistoryResponse generateImagePrompt(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        PromptHistory prompt = agent1Service.generateAndSaveImagePrompt(project, project.getDescription());
        PromptHistory saved = promptHistoryRepository.save(prompt);

        project.updateStatus(ProjectStatus.IMAGE_PROMPT_GENERATED);
        return PromptHistoryResponse.from(saved);
    }

    @Transactional
    public PromptHistoryResponse handleImagePromptFeedback(Long projectId, String feedback) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        PromptHistory latest = promptHistoryRepository.findFirstByProjectIdAndTypeOrderByVersionDesc(projectId, PromptType.IMAGE_PROMPT)
                .orElseThrow(() -> new IllegalArgumentException("No existing image prompt found for feedback"));

        String updatedScript = project.getDescription() + "\n\nUser Feedback: " + feedback;
        PromptHistory newPrompt = agent1Service.generateAndSaveImagePrompt(project, updatedScript);
        
        // Increment version
        PromptHistory versionedPrompt = PromptHistory.builder()
                .project(project)
                .type(PromptType.IMAGE_PROMPT)
                .content(newPrompt.getContent())
                .negativeContent(newPrompt.getNegativeContent())
                .version(latest.getVersion() + 1)
                .isApproved(false)
                .build();

        PromptHistory saved = promptHistoryRepository.save(versionedPrompt);
        return PromptHistoryResponse.from(saved);
    }

    @Transactional
    public ProjectDetailResponse approveImagePrompt(Long projectId, Long promptId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        PromptHistory prompt = promptHistoryRepository.findById(promptId)
                .orElseThrow(() -> new IllegalArgumentException("Prompt not found: " + promptId));

        prompt.approve();
        project.updateStatus(ProjectStatus.IMAGE_APPROVED);

        return getProjectDetail(projectId);
    }

    @Transactional
    public PromptHistoryResponse generateVideoPrompt(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        PromptHistory approvedImagePrompt = promptHistoryRepository.findFirstByProjectIdAndTypeOrderByVersionDesc(projectId, PromptType.IMAGE_PROMPT)
                .orElseThrow(() -> new IllegalArgumentException("Approved image prompt required before generating video prompt"));

        PromptHistory videoPrompt = agent2Service.generateAndSaveVideoPrompt(project, project.getDescription(), approvedImagePrompt.getContent());
        PromptHistory saved = promptHistoryRepository.save(videoPrompt);

        project.updateStatus(ProjectStatus.VIDEO_PROMPT_GENERATED);
        return PromptHistoryResponse.from(saved);
    }

    @Transactional
    public void triggerVideoGeneration(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        PromptHistory videoPrompt = promptHistoryRepository.findFirstByProjectIdAndTypeOrderByVersionDesc(projectId, PromptType.VIDEO_PROMPT)
                .orElseThrow(() -> new IllegalArgumentException("Video prompt required before video generation"));

        project.updateStatus(ProjectStatus.VIDEO_GENERATING);
        videoGenerationService.generateVideoAsync(project, videoPrompt);
    }
}
