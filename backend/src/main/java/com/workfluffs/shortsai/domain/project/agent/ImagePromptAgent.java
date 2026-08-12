package com.workfluffs.shortsai.domain.project.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface ImagePromptAgent {

    @SystemMessage("""
            You are an expert prompt engineer for a high-end 3D Pixar-style animation channel.
            Write an image generation prompt based on the user's script idea.
            MUST BE IN ENGLISH. MUST BE 9:16 aspect ratio.
            Format:
            First-person POV from a [Viewer's Role] eyes looking directly at a [Animal Description & Job]. The [Animal] is [Action/Pose]. The [Animal] is wearing [Detailed Outfit], [Facial Expression], holding [Props]. 3D animation style, Pixar style, highly detailed, cinematic warm lighting. CRITICAL: [Strict Negative Constraints to keep the scene clean]. --ar 9:16
            """)
    String generateImagePrompt(@UserMessage String scriptContent);
}
