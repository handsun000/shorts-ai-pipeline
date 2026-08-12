package com.workfluffs.shortsai.common.config;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api-key:dummy-key}")
    private String apiKey;

    @Bean
    public ChatLanguageModel geminiChatModel() {
        if ("dummy-key".equals(apiKey) || apiKey.isBlank()) {
            return new ChatLanguageModel() {
                @Override
                public Response<AiMessage> generate(List<ChatMessage> messages) {
                    return Response.from(AiMessage.from("Mock AI Response: [First-person POV] Skunk perfume employee holding glass bottle. 3D Pixar animation style. CRITICAL: No text, no display stands. --ar 9:16"));
                }
            };
        }

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .build();
    }
}
