package com.spring.ai.orchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ai.mcp.McpExecutor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AiOrchestratorServiceImpl implements AiOrchestratorService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private McpExecutor mcpExecutor;

    @Autowired
    private ToolCallbackProvider createStudentTool;

    public String handle(String query) {

        // STEP 1: Send prompt to AI with system instructions
        System.out.println("📤 STEP 1: Sending request to AI...");
        ChatResponse response = chatClient
                .prompt()
                .system("""
                    You are an enterprise onboarding assistant.

                    Available tool:
                    createStudent(name:String, age:Integer, email:String)

                    If user asks to create a student,
                    respond ONLY with this JSON:
                    {
                      "tool_name": "createStudent",
                      "arguments": {
                        "name": "...",
                        "age": ...,
                        "email": "..."
                      }
                    }
                    """)
                .user(query)
                .call()
                .chatResponse();

        // STEP 2: Validate response
        System.out.println("✅ STEP 2: Response received from AI"+ response);
        if (response == null || response.getResult() == null) {
            return "Error: Invalid response from AI model";
        }

        // STEP 3: Extract assistant message
        System.out.println("🔍 STEP 3: Extracting tool calls from response...");
        AssistantMessage assistantMessage = (AssistantMessage) response.getResult().getOutput();
        List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();

        String content = assistantMessage.getText();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json =  mapper.convertValue(content, JsonNode.class);
        try{
            json = mapper.readTree(content);
        }catch(Exception e){}
        String toolName = json.get("tool_name").asText();
        JsonNode args = json.get("arguments");

        if ("createStudent".equals(toolName)) {

            String argumentsJson = args.toString();

            AssistantMessage.ToolCall toolCall =
                    new AssistantMessage.ToolCall(
                            UUID.randomUUID().toString(),
                            "function",
                            toolName,
                            argumentsJson
                    );

            String result = mcpExecutor.execute(toolCall);

            return result;
        }
        // No tool call needed - direct response
        System.out.println("ℹ️  No tool call needed - returning direct response\n");
        return assistantMessage.getText();
    }
}
