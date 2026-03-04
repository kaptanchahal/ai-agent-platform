package com.spring.ai.controller;

import com.spring.ai.orchestrator.AiOrchestratorService;
import com.spring.ai.orchestrator.AiOrchestratorServiceImpl;
import com.spring.ai.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class StudentController {

    private final AiOrchestratorService aiOrchestratorService;

    @PostMapping("/student")
    public String ask(@RequestBody String prompt) {
        return aiOrchestratorService.handle(prompt);
    }
}
