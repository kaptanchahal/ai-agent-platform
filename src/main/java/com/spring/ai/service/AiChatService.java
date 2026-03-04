package com.spring.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    public AiChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String askAi(String prompt) {
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}
