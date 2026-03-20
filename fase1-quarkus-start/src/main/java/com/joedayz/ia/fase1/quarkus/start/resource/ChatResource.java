package com.joedayz.ia.fase1.quarkus.start.resource;

import com.joedayz.ia.fase1.quarkus.start.service.IAService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

/**
 * REST API para chat con IA.
 * 
 * Endpoints a implementar:
 * - GET /api/chat?message=...&provider=openai
 * - POST /api/chat con body JSON
 * - GET /api/chat/health para verificar que funciona
 */
@Path("/api/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {

    private static final Logger LOG = Logger.getLogger(ChatResource.class);

    @Inject
    IAService iaService;

    /**
     * Endpoint GET simple.
     * 
     * Ejemplo: GET /api/chat?message=Hola&provider=openai
     */
    @GET
    public String chatGet(
            // TODO: Agregar parámetro @QueryParam("message") String message
            // TODO: Agregar parámetro @QueryParam("provider") @DefaultValue("openai") String provider
    ) {
        // TODO: Validar que message no sea null o vacío
        // if (message == null || message.isBlank()) {
        //     throw new BadRequestException("El parámetro 'message' es requerido");
        // }
        
        // TODO: Logging
        // LOG.infof("GET /api/chat - message: %s, provider: %s", message, provider);
        
        // TODO: Llamar al servicio
        // return iaService.chat(message, provider);
        
        return "TODO: Implementar chatGet";
    }

    /**
     * Endpoint POST con body JSON.
     * 
     * Body ejemplo:
     * {
     *   "message": "Explica qué es un LLM",
     *   "provider": "anthropic"
     * }
     */
    @POST
    public String chatPost(
            // TODO: Agregar parámetro ChatRequestDTO request
    ) {
        // TODO: Validar request
        // TODO: Llamar al servicio
        // TODO: Retornar respuesta
        
        return "TODO: Implementar chatPost";
    }

    /**
     * Health check para verificar que el API funciona.
     */
    @GET
    @Path("/health")
    public String health() {
        // TODO: Retornar mensaje indicando que el servicio está activo
        return "Chat API funcionando";
    }
    
    // TODO: Crear record ChatRequestDTO
    // public record ChatRequestDTO(String message, String provider) {}
}
