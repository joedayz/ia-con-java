package com.joedayz.ia.fase1.start;

import com.joedayz.ia.common.config.EnvConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Fase 1 Start: Primera llamada a una API de IA (OpenAI o Anthropic).
 * 
 * Este archivo contiene la estructura básica. Los estudiantes completarán
 * los TODOs durante la clase.
 * 
 * Soporta dos proveedores:
 * - OpenAI (gpt-3.5-turbo, gpt-4, etc.)
 * - Anthropic (claude-3-haiku, claude-3-sonnet, etc.)
 */
public class PrimeraLlamadaIA {

    // TODO: Definir constantes para modelos por defecto
    // private static final String DEFAULT_OPENAI_MODEL = "gpt-3.5-turbo";
    // private static final String DEFAULT_ANTHROPIC_MODEL = "claude-3-haiku-20240307";

    public static void main(String[] args) throws Exception {
        // TODO: Paso 1 - Leer configuración del .env
        // - API key (OPENAI_API_KEY o ANTHROPIC_API_KEY)
        // - Base URL (OPENAI_API_BASE o ANTHROPIC_API_BASE)
        // - Determinar qué proveedor usar
        
        System.out.println("=== Primera Llamada a IA ===");
        
        // TODO: Paso 2 - Leer el mensaje del usuario
        // Si hay argumentos, usarlos. Si no, usar un mensaje por defecto
        String prompt = "Di 'Hola desde Java' en una frase.";
        
        System.out.println("Pregunta: " + prompt);
        
        // TODO: Paso 3 - Determinar qué proveedor usar
        // Verificar si existe OPENAI_API_KEY o ANTHROPIC_API_KEY
        
        // TODO: Paso 4 - Llamar al método correspondiente
        // String response = enviarChatOpenAI(...) o enviarChatAnthropic(...)
        
        // TODO: Paso 5 - Mostrar la respuesta
        // System.out.println("Respuesta: " + response);
    }

    /**
     * Envía un mensaje a OpenAI y obtiene la respuesta.
     * 
     * Endpoint: POST /chat/completions
     * Formato del body:
     * {
     *   "model": "gpt-3.5-turbo",
     *   "messages": [{"role": "user", "content": "..."}],
     *   "max_tokens": 500
     * }
     */
    public static String enviarChatOpenAI(String baseUrl, String apiKey, String model, String userMessage) 
            throws Exception {
        
        // TODO: Construir la URL completa
        // String url = baseUrl + "/chat/completions";
        
        // TODO: Construir el body JSON
        // Usar text blocks """ """ para mayor claridad
        // String body = """
        //     {
        //       "model": "%s",
        //       "messages": [
        //         {"role": "user", "content": "%s"}
        //       ],
        //       "max_tokens": 500
        //     }
        //     """.formatted(model, escapeJson(userMessage));
        
        // TODO: Crear HttpClient
        // HttpClient client = HttpClient.newBuilder()
        //     .connectTimeout(Duration.ofSeconds(15))
        //     .build();
        
        // TODO: Crear HttpRequest
        // HttpRequest request = HttpRequest.newBuilder()
        //     .uri(URI.create(url))
        //     .header("Content-Type", "application/json")
        //     .header("Authorization", "Bearer " + apiKey)
        //     .timeout(Duration.ofSeconds(30))
        //     .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
        //     .build();
        
        // TODO: Enviar request y recibir response
        // HttpResponse<String> response = client.send(request, 
        //     HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        
        // TODO: Validar status code
        // if (response.statusCode() != 200) {
        //     throw new RuntimeException("API error " + response.statusCode() + ": " + response.body());
        // }
        
        // TODO: Extraer contenido de la respuesta
        // return extraerContenidoOpenAI(response.body());
        
        return "TODO: Implementar enviarChatOpenAI";
    }

    /**
     * Envía un mensaje a Anthropic y obtiene la respuesta.
     * 
     * Endpoint: POST /messages
     * Formato del body:
     * {
     *   "model": "claude-3-haiku-20240307",
     *   "messages": [{"role": "user", "content": "..."}],
     *   "max_tokens": 500
     * }
     * 
     * NOTA: Anthropic requiere header adicional "anthropic-version: 2023-06-01"
     */
    public static String enviarChatAnthropic(String baseUrl, String apiKey, String model, String userMessage) 
            throws Exception {
        
        // TODO: Similar a OpenAI pero con diferencias:
        // 1. URL: /messages en lugar de /chat/completions
        // 2. Header adicional: "anthropic-version: 2023-06-01"
        // 3. Header: "x-api-key" en lugar de "Authorization: Bearer"
        // 4. Estructura de respuesta diferente
        
        return "TODO: Implementar enviarChatAnthropic";
    }

    /**
     * Escapa caracteres especiales para JSON.
     * Importante para evitar errores de formato.
     */
    private static String escapeJson(String s) {
        // TODO: Implementar escape de caracteres especiales
        // - Backslash (\)
        // - Comillas (")
        // - Saltos de línea (\n)
        // - Retornos de carro (\r)
        // - Tabulaciones (\t)
        
        if (s == null) return "";
        
        // Ejemplo:
        // return s.replace("\\", "\\\\")
        //         .replace("\"", "\\\"")
        //         .replace("\n", "\\n")
        //         .replace("\r", "\\r")
        //         .replace("\t", "\\t");
        
        return s; // TODO: Implementar
    }

    /**
     * Extrae el contenido del mensaje de una respuesta JSON de OpenAI.
     * 
     * Ejemplo de respuesta:
     * {
     *   "choices": [{
     *     "message": {
     *       "content": "Esta es la respuesta del modelo"
     *     }
     *   }]
     * }
     */
    private static String extraerContenidoOpenAI(String json) {
        // TODO: Parsear el JSON para extraer el contenido
        // Podemos usar parsing manual (buscar "content":"...")
        // o usar una librería JSON
        
        // Ejemplo de parsing manual:
        // String marker = "\"content\":\"";
        // int start = json.indexOf(marker);
        // ... encontrar el cierre de la comilla ...
        // return substring con el contenido
        
        return json; // TODO: Implementar
    }

    /**
     * Extrae el contenido de una respuesta JSON de Anthropic.
     * 
     * Ejemplo de respuesta:
     * {
     *   "content": [{
     *     "text": "Esta es la respuesta del modelo"
     *   }]
     * }
     */
    private static String extraerContenidoAnthropic(String json) {
        // TODO: Similar a OpenAI pero buscar "text":"..." dentro de "content"
        
        return json; // TODO: Implementar
    }
}
