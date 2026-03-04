package com.spring.ai.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ai.entity.ToolAudit;
import com.spring.ai.repository.ToolAuditRepository;
import com.spring.ai.tools.StudentToolService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class McpExecutor {

    private final StudentToolService studentToolService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(McpExecutor.class);
    private final ToolAuditRepository toolAuditRepository;

    public String execute(AssistantMessage.ToolCall toolCall) {
        if (toolCall == null || toolCall.name() == null) {
            log.error("Invalid tool call: toolCall or name is null");
            return "Error: Invalid tool call";
        }
        String result = "";
        try {
            switch (toolCall.name()) {
                case "createStudent" -> {
                    Map<String, Object> args = objectMapper.readValue(
                            toolCall.arguments().toString(),
                            Map.class
                    );

                    if (args == null || !args.containsKey("name") || !args.containsKey("age")) {
                        result = "Error: Missing required parameters (name, age)";
                        log.error("Missing parameters in createStudent tool call");
                        break;
                    }

                    String name = args.get("name").toString();
                    Integer age = Integer.valueOf(args.get("age").toString());
                    String email = args.get("email").toString();

                    result = studentToolService.createStudent(name, age, email);
                    log.info("Student created successfully: {}", name);

                }
                default -> {
                    result = "Error: Unknown tool - " + toolCall.name();
                    log.warn("Unknown tool called: {}", toolCall.name());
                }
            }
        } catch (JsonProcessingException e) {
            result = "Error: Failed to parse tool arguments - " + e.getMessage();
            log.error("JSON parsing error in tool execution", e);
        } catch (NumberFormatException e) {
            result = "Error: Invalid age format - " + e.getMessage();
            log.error("Number format error in tool execution", e);
        } catch (Exception e) {
            result = "Error: Tool execution failed - " + e.getMessage();
            log.error("Unexpected error in tool execution", e);
        }

        saveAudit(toolCall, result);
        return result;
    }

    private void saveAudit(AssistantMessage.ToolCall toolCall, String result) {
        try {
            ToolAudit audit = new ToolAudit();
            audit.setToolName(toolCall.name());
            audit.setArguments(toolCall.arguments().toString());
            audit.setResult(result);
            audit.setExecutedAt(LocalDateTime.now());
            toolAuditRepository.save(audit);
            log.info("Audit saved for tool: {}", toolCall.name());
        } catch (Exception e) {
            log.error("Failed to save audit for tool: {}", toolCall.name(), e);
        }
    }
}
