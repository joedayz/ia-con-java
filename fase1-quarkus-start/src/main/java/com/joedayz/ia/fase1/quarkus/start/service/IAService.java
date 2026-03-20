package com.joedayz.ia.fase1.quarkus.start.service;

import com.joedayz.ia.fase1.quarkus.start.config.IAConfig;
import com.joedayz.ia.fase1.quarkus.start.model.ChatRequest;
import com.joedayz.ia.fase1.quarkus.start.model.ChatResponse;
import com.joedayz.ia.fase1.quarkus.start.model.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Servicio para interactuar con APIs de IA (OpenAI y Anthropic).
 * 
 * En clase implementaremos:
 * 1. Método chat() que detecte el proveedor
 * 2. Método específico para OpenAI
 * 3. Método específico para Anthropic
 * 4. Manejo de errores
 */
@ApplicationScoped
public class IAService {

    private static final Logger LOG = Logger.getLogger(IAService.class);

    @Inject
    IAConfig config;

    /**
     * Envía un mensaje a la IA usando el proveedor especificado.
     * 
     * @param userMessage Mensaje del usuario
     * @param provider "openai" o "anthropic"
     * @return Respuesta de la IA
     */
    public String chat(String userMessage, String provider) {
        // TODO: Validar parámetros
        // if (userMessage == null || userMessage.isBlank()) {
        //     throw new IllegalArgumentException("El mensaje no puede estar vacío");
        // }
        
        // TODO: Determinar qué método llamar según el proveedor
        // if ("openai".equalsIgnoreCase(provider)) {
        //     return chatOpenAI(userMessage);
        // } else if ("anthropic".equalsIgnoreCase(provider)) {
        //     return chatAnthropic(userMessage);
        // } else {
        //     throw new IllegalArgumentException("Proveedor no soportado: " + provider);
        // }
        
        return "TODO: Implementar chat";
    }

    /**
     * Llama a la API de OpenAI.
     * 
     * Endpoint: POST https://api.openai.com/v1/chat/completions
     * Headers:
     * - Authorization: Bearer {api_key}
     * - Content-Type: application/json
     */
    private String chatOpenAI(String userMessage) {
        // TODO: Obtener configuración
        // String apiKey = config.openai().key().orElseThrow(...);
        // String baseUrl = config.openai().base();
        // String model = config.openai().model();
        
        // TODO: Construir request
        // List<Message> messages = List.of(Message.user(userMessage));
        // ChatRequest request = new ChatRequest(model, messages, config.openai().maxTokens());
        
        // TODO: Crear cliente HTTP
        // try (Client client = ClientBuilder.newBuilder().build()) {
        //     String url = baseUrl + "/chat/completions";
        //     
        //     ChatResponse response = client
        //         .target(url)
        //         .request(MediaType.APPLICATION_JSON)
        //         .header("Authorization", "Bearer " + apiKey)
        //         .post(Entity.json(request), ChatResponse.class);
        //     
        //     return response.getContent("openai");
        // }
        
        LOG.info("Llamando a OpenAI...");
        return "TODO: Implementar chatOpenAI";
    }

    /**
     * Llama a la API de Anthropic.
     * 
     * Endpoint: POST https://api.anthropic.com/v1/messages
     * Headers:
     * - x-api-key: {api_key}
     * - anthropic-version: 2023-06-01
     * - Content-Type: application/json
     */
    private String chatAnthropic(String userMessage) {
        // TODO: Similar a OpenAI pero con diferencias en headers
        // Header: "x-api-key" en lugar de "Authorization: Bearer"
        // Header adicional: "anthropic-version"
        // Endpoint: /messages en lugar de /chat/completions
        
        LOG.info("Llamando a Anthropic...");
        return "TODO: Implementar chatAnthropic";
    }
}
