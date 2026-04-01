package com.joedayz.ia.springai.service;

import com.joedayz.ia.springai.dto.RagRequest;
import com.joedayz.ia.springai.dto.RagResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * RAG basico en memoria:
 * 1) recupera fragmentos similares,
 * 2) los inyecta en el prompt,
 * 3) genera una respuesta con citas.
 */
@Service
public class RagService {

    private static final int DEFAULT_TOP_K = 4;
    private static final int MAX_TOP_K = 8;

    private final ChatClient chatClient;
    private final SemanticSearchService semanticSearchService;

    public RagService(ChatModel chatModel, SemanticSearchService semanticSearchService) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.semanticSearchService = semanticSearchService;
    }

    public RagResponse answer(RagRequest request) {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()) {
            throw new IllegalArgumentException("query es obligatorio");
        }

        int topK = normalizeTopK(request.getTopK());
        String question = request.getQuery().trim();
        List<Document> contextDocs = semanticSearchService.buscarDocumentos(question, topK);

        if (contextDocs.isEmpty()) {
            return new RagResponse(
                    question,
                    "No encontré contexto suficiente en el vector store. Primero indexa documentos con /api/buscar/demo o /api/buscar/pdf.",
                    topK,
                    false,
                    List.of());
        }

        String answer = chatClient.prompt()
                .system(buildSystemPrompt())
                .user(buildUserPrompt(question, contextDocs))
                .call()
                .content();

        return new RagResponse(question, answer, topK, true, toCitations(contextDocs));
    }

    private int normalizeTopK(Integer requestedTopK) {
        if (requestedTopK == null) {
            return DEFAULT_TOP_K;
        }
        return Math.max(1, Math.min(requestedTopK, MAX_TOP_K));
    }

    private String buildSystemPrompt() {
        return """
                Eres un asistente de RAG para un curso de IA con Java.
                Responde solo con base en el contexto recuperado.
                Si el contexto no alcanza, dilo explicitamente.
                Usa citas inline como [1], [2], [3] segun los fragmentos proporcionados.
                No inventes fuentes ni afirmaciones fuera del contexto.
                Responde en espanol claro y didactico.
                """;
    }

    private String buildUserPrompt(String question, List<Document> contextDocs) {
        return """
                Pregunta del usuario:
                %s

                Contexto recuperado:
                %s

                Instrucciones:
                - Responde usando el contexto recuperado.
                - Cita evidencia en el texto con [1], [2], etc.
                - Si dos fragmentos se complementan, combinalos.
                - Si el contexto es insuficiente, indicalo.
                """.formatted(question, formatContext(contextDocs));
    }

    private String formatContext(List<Document> docs) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < docs.size(); i++) {
            Document doc = docs.get(i);
            builder.append("[")
                    .append(i + 1)
                    .append("] Fuente: ")
                    .append(resolveSource(doc.getMetadata()))
                    .append('\n')
                    .append(doc.getText())
                    .append("\n\n");
        }
        return builder.toString().trim();
    }

    private String resolveSource(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "sin-metadata";
        }
        Object source = metadata.get("fuente");
        if (source != null) {
            return String.valueOf(source);
        }
        Object tema = metadata.get("tema");
        return tema != null ? String.valueOf(tema) : "sin-fuente";
    }

    private List<RagResponse.Citation> toCitations(List<Document> docs) {
        return java.util.stream.IntStream.range(0, docs.size())
                .mapToObj(i -> {
                    Document doc = docs.get(i);
                    return new RagResponse.Citation(i + 1, excerpt(doc.getText()), doc.getMetadata());
                })
                .toList();
    }

    private String excerpt(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= 220 ? text : text.substring(0, 220) + "...";
    }
}

