package com.joedayz.ia.fase5.mcp.consumer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class McpGatewayService {

    private static final Pattern TOOL_NAME_PATTERN = Pattern.compile("\\\"name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    private final ChatClient chatClient;
    private final ToolCallbackProvider toolCallbackProvider;
    private final ObjectMapper objectMapper;
    private final String mcpServerUrl;

    public McpGatewayService(
        ChatClient.Builder chatClientBuilder,
        ToolCallbackProvider toolCallbackProvider,
        ObjectMapper objectMapper,
        @Value("${spring.ai.mcp.client.sse.connections.fase5-provider.url:http://localhost:8091}") String mcpServerUrl
    ) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbackProvider = toolCallbackProvider;
        this.objectMapper = objectMapper;
        this.mcpServerUrl = mcpServerUrl;
    }

    public String ask(String userMessage) {
        String answer;
        try {
            answer = callModel(userMessage);
        } catch (Exception callError) {
            return "No pude procesar la consulta en este momento. Verifica que el provider MCP este activo y vuelve a intentar.";
        }

        if (!looksLikeToolCallPayload(answer)) {
            return answer;
        }

        String fallback = executeToolFromPayload(answer);
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }

        return "No pude ejecutar la herramienta correctamente. Intenta con /demo/cronograma/{clase} o /demo/modulo/{fase}.";
    }

    private String callModel(String userMessage) {
        return chatClient.prompt()
            .system("""
                Eres un asistente del curso IA con Java.
                Debes usar las herramientas MCP disponibles cuando el usuario pida datos del cronograma,
                modulos o actividades de fase5.
                Si usas una herramienta, NO devuelvas JSON ni metadatos de tool calling.
                Siempre responde solo con el resultado final para el usuario.
                Responde en espanol y de forma concreta.
                """)
            .user(userMessage)
            .toolCallbacks(toolCallbackProvider)
            .call()
            .content();
    }

    private boolean looksLikeToolCallPayload(String answer) {
        if (answer == null) {
            return false;
        }
        return answer.contains("\"name\"") && answer.contains("\"parameters\"");
    }

    private String executeToolFromPayload(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String toolName = root.path("name").asText("");
            JsonNode paramsNode = root.path("parameters");
            if (toolName.isBlank() || paramsNode.isMissingNode()) {
                return null;
            }

            Map<String, Object> args = normalizeArguments(paramsNode);
            return callMcpTool(toolName, args);
        } catch (Exception parseError) {
            String toolName = extractToolName(payload);
            if (toolName == null) {
                return null;
            }
            return callMcpTool(toolName, Map.of());
        }
    }

    private Map<String, Object> normalizeArguments(JsonNode paramsNode) {
        Map<String, Object> args = objectMapper.convertValue(paramsNode, new TypeReference<>() {});
        Map<String, Object> normalized = new HashMap<>();

        for (Map.Entry<String, Object> entry : args.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Map<?, ?> valueMap && valueMap.containsKey("value")) {
                normalized.put(entry.getKey(), valueMap.get("value"));
            } else {
                normalized.put(entry.getKey(), value);
            }
        }

        if (normalized.containsKey("clase")) {
            String clase = String.valueOf(normalized.get("clase"));
            normalized.put("clase", clase.replaceAll("[^0-9]", ""));
        }

        return normalized;
    }

    private String callMcpTool(String toolName, Map<String, Object> args) {
        McpSyncClient client = null;
        try {
            client = McpClient.sync(HttpClientSseClientTransport.builder(mcpServerUrl).build()).build();
            client.initialize();
            CallToolResult result = client.callTool(new CallToolRequest(toolName, args));
            return result.toString();
        } catch (Exception ex) {
            return null;
        } finally {
            if (client != null) {
                client.closeGracefully();
            }
        }
    }

    private String extractToolName(String payload) {
        Matcher matcher = TOOL_NAME_PATTERN.matcher(payload);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

