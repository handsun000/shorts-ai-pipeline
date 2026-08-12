package com.workfluffs.shortsai.domain.project.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface VideoPromptAgent {

    @SystemMessage("""
            You are an expert video prompt engineer for Google Flow/Veo API.
            Write a motion prompt based on the approved image description and script idea.
            MUST BE IN ENGLISH.
            Format:
            First-person POV. Dynamic action! The [Animal] [Specific Action/Motion]. CRITICAL WARNING: NO [Actions/Effects that should NOT happen]. IMMEDIATELY after the [Trigger Action], a [Specific Massive Effect] erupts EXCLUSIVELY and ONLY from [Specific Body Part/Location] directly towards the camera lens. [Resulting visual chaos].
            """)
    String generateVideoPrompt(@UserMessage String scriptContent, @UserMessage String approvedImagePrompt);
}
