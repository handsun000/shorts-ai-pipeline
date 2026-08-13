package com.workfluffs.shortsai.domain.project.service;

import com.workfluffs.shortsai.domain.project.agent.IdeaAgent;
import com.workfluffs.shortsai.domain.project.agent.ImagePromptAgent;
import com.workfluffs.shortsai.domain.project.entity.Project;
import com.workfluffs.shortsai.domain.project.entity.PromptHistory;
import com.workfluffs.shortsai.domain.project.enums.PromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Agent1Service {

    private final IdeaAgent ideaAgent;
    private final ImagePromptAgent imagePromptAgent;

    public String generateIdea(String request) {
        return ideaAgent.generateIdea(request);
    }

    @Transactional
    public PromptHistory generateAndSaveImagePrompt(Project project, Integer cutOrder, String cutDescription) {
        String fullPrompt = imagePromptAgent.generateImagePrompt(cutDescription);
        
        String positivePrompt = fullPrompt;
        String negativePrompt = "";
        
        int criticalIndex = fullPrompt.indexOf("CRITICAL:");
        if (criticalIndex != -1) {
            positivePrompt = fullPrompt.substring(0, criticalIndex).trim();
            negativePrompt = fullPrompt.substring(criticalIndex + "CRITICAL:".length()).trim();
            
            // Remove --ar 9:16 from negative prompt if it exists there, or just keep it in positive
            if (negativePrompt.contains("--ar 9:16")) {
                negativePrompt = negativePrompt.replace("--ar 9:16", "").trim();
                positivePrompt += " --ar 9:16";
            }
        }
        
        return PromptHistory.builder()
                .project(project)
                .type(PromptType.IMAGE_PROMPT)
                .content(positivePrompt)
                .negativeContent(negativePrompt)
                .cutOrder(cutOrder)
                .version(1)
                .isApproved(false)
                .build();
    }
}
