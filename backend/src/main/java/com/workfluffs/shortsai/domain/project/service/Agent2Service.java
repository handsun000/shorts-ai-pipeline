package com.workfluffs.shortsai.domain.project.service;

import com.workfluffs.shortsai.domain.project.agent.VideoPromptAgent;
import com.workfluffs.shortsai.domain.project.entity.Project;
import com.workfluffs.shortsai.domain.project.entity.PromptHistory;
import com.workfluffs.shortsai.domain.project.enums.PromptType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Agent2Service {

    private final VideoPromptAgent videoPromptAgent;

    @Transactional
    public PromptHistory generateAndSaveVideoPrompt(Project project, String scriptContent, String approvedImagePrompt) {
        String fullPrompt = videoPromptAgent.generateVideoPrompt(scriptContent, approvedImagePrompt);
        
        String positivePrompt = fullPrompt;
        String negativePrompt = "";
        
        int criticalIndex = fullPrompt.indexOf("CRITICAL WARNING:");
        if (criticalIndex != -1) {
            // Find where the CRITICAL WARNING ends (usually at the next period before IMMEDIATELY)
            int immediatelyIndex = fullPrompt.indexOf("IMMEDIATELY", criticalIndex);
            if(immediatelyIndex != -1) {
                negativePrompt = fullPrompt.substring(criticalIndex + "CRITICAL WARNING:".length(), immediatelyIndex).trim();
                positivePrompt = fullPrompt.substring(0, criticalIndex).trim() + " " + fullPrompt.substring(immediatelyIndex).trim();
            } else {
                positivePrompt = fullPrompt.substring(0, criticalIndex).trim();
                negativePrompt = fullPrompt.substring(criticalIndex + "CRITICAL WARNING:".length()).trim();
            }
        }
        
        return PromptHistory.builder()
                .project(project)
                .type(PromptType.VIDEO_PROMPT)
                .content(positivePrompt)
                .negativeContent(negativePrompt)
                .version(1)
                .isApproved(false)
                .build();
    }
}
