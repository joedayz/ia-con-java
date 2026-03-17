package com.joedayz.ia.fase1.quarkus.resource;

import com.joedayz.ia.fase1.quarkus.api.ChatApiRequest;
import com.joedayz.ia.fase1.quarkus.api.ChatApiResponse;
import com.joedayz.ia.fase1.quarkus.config.OpenAIConfig;
import com.joedayz.ia.fase1.quarkus.service.OpenAIService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

/**
 * REST API para interactuar con OpenAI
 * 
 * Endpoints disponibles:
 * - GET /api/chat?message=texto : Envía un mensaje simple
 * - POST /api/chat : Envía un mensaje con opcional system prompt
 */
@Path("/api/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {

    private static final Logger LOG = Logger.getLogger(ChatResource.class);

    @Inject
    OpenAIService openAIService;

    @Inject
    OpenAIConfig config;

    /**
     * Endpoint GET simple: /api/chat?message=Hola
     */
    @GET
    public ChatApiResponse chatGet(@QueryParam("message") String message) {
        if (message == null || message.isBlank()) {
            throw new BadRequestException("El parámetro 'message' es requerido");
        }
        
        LOG.infof("GET /api/chat - message: %s", message);
        String response = openAIService.chat(message);
        return ChatApiResponse.of(response, config.model());
    }

    /**
     * Endpoint POST con body JSON:
     * {
     *   "message": "Explica qué es un LLM",
     *   "system_prompt": "Eres un profesor de IA"
     * }
     */
    @POST
    public ChatApiResponse chatPost(ChatApiRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            throw new BadRequestException("El campo 'message' es requerido");
        }
        
        LOG.infof("POST /api/chat - message: %s, systemPrompt: %s", 
            request.message(), 
            request.systemPrompt() != null ? "presente" : "ausente");
        
        String response = openAIService.chat(request.message(), request.systemPrompt());
        return ChatApiResponse.of(response, config.model());
    }

    /**
     * Health check
     */
    @GET
    @Path("/health")
    public String health() {
        return "Chat API está funcionando - Modelo: " + config.model();
    }
}
