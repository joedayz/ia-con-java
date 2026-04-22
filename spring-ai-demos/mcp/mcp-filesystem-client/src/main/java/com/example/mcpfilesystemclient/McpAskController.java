package com.example.mcpfilesystemclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpAskController {

    private static final String ALLOWED_DIR = "/Users/josediaz/mcp-playground";

    private final ChatClient chatClient;

    public McpAskController(ChatClient.Builder chatClientBuilder,
                            ToolCallbackProvider tools) { 
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(tools)  
                .build();
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody Question question) {
        return chatClient.prompt()
                .user(withFilesystemScope(question.question()))
                .call()
                .entity(Answer.class);
    }

    private String withFilesystemScope(String userQuestion) {
        return "Filesystem scope rule: you can only access files under " + ALLOWED_DIR + ". "
                + "If the user gives a filename without a path, treat it as " + ALLOWED_DIR + "/<filename>. "
                + "Never use the current working directory for file operations. User request: " + userQuestion;
    }

    public record Question(String question) { }

    public record Answer(String answer) { }

}
