package com.joedayz.ia.fase5.mcp.consumer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class McpGatewayService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider toolCallbackProvider;

    public McpGatewayService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public String ask(String userMessage) {
        return chatClient.prompt()
            .system("""
                Eres un asistente del curso IA con Java.
                Debes usar las herramientas MCP disponibles cuando el usuario pida datos del cronograma,
                modulos o actividades de fase5.
                Responde en espanol y de forma concreta.
                """)
            .user(userMessage)
            .toolCallbacks(toolCallbackProvider)
            .call()
            .content();
    }
}

