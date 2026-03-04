package com.spring.ai.config;

import com.spring.ai.tools.StudentToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider onboardingTools){
        return builder
                .defaultOptions(ChatOptions.builder()
                       // .model("qwen2.5:7b")
                       // .temperature(0.1)
                        //    .maxTokens(1000)
                        .build())
                // Automatic tool execution disabled for manual control
                //.defaultToolCallbacks(onboardingTools)  // ❌ Commented for manual execution
                .build();
    }

    @Bean
    public ToolCallbackProvider createStudent(StudentToolService studentToolService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(studentToolService)
                .build();
    }
}
