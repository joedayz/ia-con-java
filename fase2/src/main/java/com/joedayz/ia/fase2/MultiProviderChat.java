package com.joedayz.ia.fase2;

import com.joedayz.ia.common.config.EnvConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Clase base para realizar llamadas a múltiples proveedores de IA.
 * Soporta: OpenAI, Anthropic (Claude) y Google Gemini.
 */
public class MultiProviderChat {

    private static final String OPENAI_MODEL = "gpt-3.5-turbo";
    private static final String ANTHROPIC_MODEL = "claude-3-haiku-20240307";
    private static final String GEMINI_MODEL = "gemini-2.5-flash";

    private final String provider;
    private final HttpClient httpClient;

    public MultiProviderChat(String provider) {
        this.provider = provider != null ? provider : detectarProveedor();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public MultiProviderChat() {
        this(null);
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderName() {
        return switch (provider.toLowerCase()) {
            case "openai" -> "OpenAI (GPT-3.5)";
            case "anthropic" -> "Anthropic (Claude 3 Haiku)";
            case "gemini" -> "Google Gemini 2.5 Flash";
            default -> provider;
        };
    }

    /**
     * Autodetecta el proveedor basándose en las API keys disponibles.
     */
    private static String detectarProveedor() {
        if (EnvConfig.hasKey("OPENAI_API_KEY")) {
            return "openai";
        } else if (EnvConfig.hasKey("GEMINI_API_KEY")) {
            return "gemini";
        } else if (EnvConfig.hasKey("ANTHROPIC_API_KEY")) {
            return "anthropic";
        } else {
            throw new IllegalStateException(
                "No se encontró ninguna API key. " +
                "Configura OPENAI_API_KEY, GEMINI_API_KEY o ANTHROPIC_API_KEY en .env");
        }
    }

    /**
     * Realiza una llamada al LLM con un mensaje de usuario.
     */
    public String chat(String userMessage) throws Exception {
        return chat(null, userMessage, 800, 0.7);
    }

    /**
     * Realiza una llamada al LLM con system prompt y mensaje de usuario.
     */
    public String chat(String systemPrompt, String userMessage) throws Exception {
        return chat(systemPrompt, userMessage, 800, 0.7);
    }

    /**
     * Realiza una llamada al LLM con todos los parámetros.
     */
    public String chat(String systemPrompt, String userMessage, int maxTokens, double temperature) 
            throws Exception {
        return switch (provider.toLowerCase()) {
            case "openai" -> chatOpenAI(systemPrompt, userMessage, maxTokens, temperature);
            case "anthropic" -> chatAnthropic(systemPrompt, userMessage, maxTokens, temperature);
            case "gemini" -> chatGemini(systemPrompt, userMessage, maxTokens, temperature);
            default -> throw new IllegalArgumentException("Proveedor desconocido: " + provider);
        };
    }

    // ═══════════════════════════════════════════════════════
    // OPENAI
    // ═══════════════════════════════════════════════════════

    private String chatOpenAI(String systemPrompt, String userMessage, int maxTokens, double temperature) 
            throws Exception {
        String apiKey = EnvConfig.getOpenAiApiKey();
        String baseUrl = EnvConfig.getOpenAiApiBase();
        String url = baseUrl.endsWith("/") 
            ? baseUrl + "chat/completions" 
            : baseUrl + "/chat/completions";

        // Construir mensajes
        StringBuilder messagesJson = new StringBuilder();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messagesJson.append(String.format(
                "{\"role\": \"system\", \"content\": \"%s\"},", 
                escapeJson(systemPrompt)));
        }
        messagesJson.append(String.format(
            "{\"role\": \"user\", \"content\": \"%s\"}", 
            escapeJson(userMessage)));

        String body = String.format("""
            {
              "model": "%s",
              "messages": [%s],
              "max_tokens": %d,
              "temperature": %.1f
            }
            """, OPENAI_MODEL, messagesJson, maxTokens, temperature);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API error " + response.statusCode() + 
                ": " + response.body());
        }

        return extraerContenidoOpenAI(response.body());
    }

    private String extraerContenidoOpenAI(String json) {
        String marker = "\"content\"";
        int start = json.indexOf(marker);
        if (start == -1) return json;
        
        int colonPos = json.indexOf(':', start + marker.length());
        if (colonPos == -1) return json;
        
        int firstQuote = json.indexOf('"', colonPos);
        if (firstQuote == -1) return json;
        
        start = firstQuote + 1;
        int end = start;
        
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') end += 2;
            else if (c == '"') break;
            else end++;
        }
        
        return json.substring(start, end)
            .replace("\\n", "\n")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }

    // ═══════════════════════════════════════════════════════
    // ANTHROPIC (Claude)
    // ═══════════════════════════════════════════════════════

    private String chatAnthropic(String systemPrompt, String userMessage, int maxTokens, double temperature) 
            throws Exception {
        String apiKey = EnvConfig.getAnthropicApiKey();
        String baseUrl = EnvConfig.getAnthropicApiBase();
        String url = baseUrl.endsWith("/") 
            ? baseUrl + "messages" 
            : baseUrl + "/messages";

        // En Anthropic, el system prompt va en un campo separado
        String systemField = (systemPrompt != null && !systemPrompt.isBlank())
            ? String.format(",\"system\": \"%s\"", escapeJson(systemPrompt))
            : "";

        String body = String.format("""
            {
              "model": "%s",
              "messages": [
                {"role": "user", "content": "%s"}
              ],
              "max_tokens": %d,
              "temperature": %.1f%s
            }
            """, ANTHROPIC_MODEL, escapeJson(userMessage), maxTokens, temperature, systemField);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Anthropic API error " + response.statusCode() + 
                ": " + response.body());
        }

        return extraerContenidoAnthropic(response.body());
    }

    private String extraerContenidoAnthropic(String json) {
        String marker = "\"text\"";
        int start = json.indexOf(marker);
        if (start == -1) return json;
        
        int colonPos = json.indexOf(':', start + marker.length());
        if (colonPos == -1) return json;
        
        int firstQuote = json.indexOf('"', colonPos);
        if (firstQuote == -1) return json;
        
        start = firstQuote + 1;
        int end = start;
        
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') end += 2;
            else if (c == '"') break;
            else end++;
        }
        
        return json.substring(start, end)
            .replace("\\n", "\n")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }

    // ═══════════════════════════════════════════════════════
    // GOOGLE GEMINI
    // ═══════════════════════════════════════════════════════

    private String chatGemini(String systemPrompt, String userMessage, int maxTokens, double temperature) 
            throws Exception {
        String apiKey = EnvConfig.getGeminiApiKey();
        String baseUrl = EnvConfig.getGeminiApiBase();
        String url = baseUrl.endsWith("/")
            ? baseUrl + "models/" + GEMINI_MODEL + ":generateContent?key=" + apiKey
            : baseUrl + "/models/" + GEMINI_MODEL + ":generateContent?key=" + apiKey;

        // En Gemini, el system prompt se envía como instrucción del sistema
        String textoCompleto = systemPrompt != null && !systemPrompt.isBlank()
            ? systemPrompt + "\n\n" + userMessage
            : userMessage;

        String body = String.format("""
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }],
              "generationConfig": {
                "temperature": %.1f,
                "maxOutputTokens": %d
              }
            }
            """, escapeJson(textoCompleto), temperature, maxTokens);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API error " + response.statusCode() +
                ": " + response.body());
        }

        return extraerContenidoGemini(response.body());
    }

    private String extraerContenidoGemini(String json) {
        String marker = "\"text\"";
        int start = json.indexOf(marker);
        if (start == -1) return json;

        int colonPos = json.indexOf(':', start + marker.length());
        if (colonPos == -1) return json;

        int firstQuote = json.indexOf('"', colonPos);
        if (firstQuote == -1) return json;

        start = firstQuote + 1;
        int end = start;

        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') end += 2;
            else if (c == '"') break;
            else end++;
        }

        return json.substring(start, end)
            .replace("\\n", "\n")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }

    // ═══════════════════════════════════════════════════════
    // UTILIDADES
    // ═══════════════════════════════════════════════════════

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Obtiene información del proveedor disponible.
     */
    public static String getProviderInfo() {
        String provider = null;
        try {
            provider = detectarProveedor();
        } catch (IllegalStateException e) {
            return "❌ No hay API keys configuradas";
        }

        StringBuilder info = new StringBuilder("Proveedores disponibles:\n");
        
        if (EnvConfig.hasKey("OPENAI_API_KEY")) {
            info.append("  ✓ OpenAI (GPT-3.5)");
            if ("openai".equals(provider)) info.append(" [ACTIVO]");
            info.append("\n");
        }
        
        if (EnvConfig.hasKey("ANTHROPIC_API_KEY")) {
            info.append("  ✓ Anthropic (Claude)");
            if ("anthropic".equals(provider)) info.append(" [ACTIVO]");
            info.append("\n");
        }
        
        if (EnvConfig.hasKey("GEMINI_API_KEY")) {
            info.append("  ✓ Google Gemini");
            if ("gemini".equals(provider)) info.append(" [ACTIVO]");
            info.append("\n");
        }

        return info.toString();
    }
}
