package com.joedayz.ia.fase1.quarkus.start.resource;

import com.joedayz.ia.fase1.quarkus.start.config.IAConfig;
import com.joedayz.ia.fase1.quarkus.start.service.IAService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * REST API para chat con Ollama (llama3.2).
 *
 * Endpoints:
 * - GET /api/chat?message=texto
 * - POST /api/chat con JSON: {"message": "...", "system_prompt": "..."}
 * - GET /api/chat/health
 */
@Path("/api/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {

    private static final Logger LOG = Logger.getLogger(ChatResource.class);

    @Inject
    IAService iaService;

    @Inject
    IAConfig config;

    @GET
    public Map<String, Object> chatGet(
            @QueryParam("message") String message) {
        if (message == null || message.isBlank()) {
            throw new BadRequestException("El parámetro 'message' es requerido");
        }

        LOG.infof("GET /api/chat - message: %s", message);
        String response = iaService.chat(message);
        return Map.of(
            "response", response,
            "provider", "ollama",
            "model", config.ollama().model(),
            "timestamp", System.currentTimeMillis()
        );
    }

    @POST
    public Map<String, Object> chatPost(Map<String, String> request) {
        String message = request.get("message");
        String systemPrompt = request.get("system_prompt");

        if (message == null || message.isBlank()) {
            throw new BadRequestException("El campo 'message' es requerido");
        }

        LOG.infof("POST /api/chat - message: %s, systemPrompt: %s",
            message, systemPrompt != null ? "presente" : "ausente");

        String response = iaService.chat(message, systemPrompt);
        return Map.of(
            "response", response,
            "provider", "ollama",
            "model", config.ollama().model(),
            "timestamp", System.currentTimeMillis()
        );
    }

    @GET
    @Path("/health")
    public String health() {
        return "Chat API funcionando - Modelo: " + config.ollama().model();
    }
}
