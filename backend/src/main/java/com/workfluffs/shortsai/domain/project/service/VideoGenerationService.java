package com.workfluffs.shortsai.domain.project.service;

import com.workfluffs.shortsai.domain.asset.entity.Asset;
import com.workfluffs.shortsai.domain.asset.enums.AssetStatus;
import com.workfluffs.shortsai.domain.asset.enums.AssetType;
import com.workfluffs.shortsai.domain.asset.service.FileStorageService;
import com.workfluffs.shortsai.domain.project.controller.VideoSseController;
import com.workfluffs.shortsai.domain.project.entity.Project;
import com.workfluffs.shortsai.domain.project.entity.PromptHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoGenerationService {

    private final VideoSseController videoSseController;
    private final FileStorageService fileStorageService;

    @Async
    @Transactional
    public void generateVideoAsync(Project project, PromptHistory videoPrompt) {
        log.info("Starting video generation for project: {}", project.getId());
        
        try {
            // Notify frontend that generation started
            videoSseController.notifyStatus(project.getId(), AssetStatus.GENERATING, null);
            
            // TODO: Replace with actual Google Veo / Vertex AI Video API call
            // Simulating long-running generation process
            Thread.sleep(10000); 
            
            // Simulate saving a generated video file (dummy data)
            byte[] dummyVideoData = "dummy video content".getBytes();
            String savedPath = fileStorageService.saveFile(dummyVideoData, ".mp4");
            
            log.info("Video generated and saved to: {}", savedPath);
            
            // Notify frontend that generation succeeded
            videoSseController.notifyStatus(project.getId(), AssetStatus.SUCCESS, savedPath);
            
        } catch (Exception e) {
            log.error("Video generation failed for project: {}", project.getId(), e);
            // Notify frontend that generation failed
            videoSseController.notifyStatus(project.getId(), AssetStatus.FAILED, null);
        }
    }
}
