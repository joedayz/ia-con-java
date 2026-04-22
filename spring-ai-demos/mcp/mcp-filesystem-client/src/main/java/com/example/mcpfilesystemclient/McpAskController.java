package com.example.mcpfilesystemclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpAskController {

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
                .user(question.question())
                .call()
                .entity(Answer.class);
    }

    public record Question(String question) { }

    public record Answer(String answer) { }

}
