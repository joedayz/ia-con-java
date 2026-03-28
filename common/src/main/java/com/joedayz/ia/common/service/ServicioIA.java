package com.joedayz.ia.common.service;

import com.joedayz.ia.common.config.EnvConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Servicio de IA reutilizable (compartido por varios proyectos del curso).
 * Encapsula las llamadas al LLM para usarlo desde cualquier fase.
 */
public class ServicioIA {

    private final String baseUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final String model;

    public ServicioIA() {
        // Prioridad: intentar OpenAI primero, luego Anthropic
        String anthropicKey = EnvConfig.get("ANTHROPIC_API_KEY");
        String openaiKey = EnvConfig.get("OPENAI_API_KEY");
        
        if (openaiKey != null && !openaiKey.isBlank()) {
            // Usar OpenAI si está configurado
            this.baseUrl = EnvConfig.getOpenAiApiBase().endsWith("/") 
                ? EnvConfig.getOpenAiApiBase() 
                : EnvConfig.getOpenAiApiBase() + "/";
            this.apiKey = openaiKey;
            this.model = "gpt-3.5-turbo";
        } else if (anthropicKey != null && !anthropicKey.isBlank()) {
            // Fallback a Anthropic
            String base = EnvConfig.get("ANTHROPIC_API_BASE", "https://api.anthropic.com/v1");
            this.baseUrl = base.endsWith("/") ? base : base + "/";
            this.apiKey = anthropicKey;
            this.model = "claude-3-haiku-20240307";
        } else {
            throw new IllegalStateException("Falta OPENAI_API_KEY o ANTHROPIC_API_KEY en .env");
        }
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    /** Constructor para proveedores personalizados. */
    public ServicioIA(String baseUrl, String apiKey, String model) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }
    
    /** Factory method: OpenAI. */
    public static ServicioIA openai() {
        return new ServicioIA(
            EnvConfig.getOpenAiApiBase(),
            EnvConfig.getOpenAiApiKey(),
            "gpt-3.5-turbo"
        );
    }
    
    /** Factory method: Anthropic Claude. */
    public static ServicioIA anthropic() {
        String base = EnvConfig.get("ANTHROPIC_API_BASE", "https://api.anthropic.com/v1");
        return new ServicioIA(
            base,
            EnvConfig.getAnthropicApiKey(),
            "claude-3-haiku-20240307"
        );
    }

    public String chat(String userMessage) {
        return chat(null, userMessage);
    }

    public String chat(String systemPrompt, String userMessage) {
        try {
            boolean isAnthropic = baseUrl.contains("anthropic.com");
            String body;
            
            if (isAnthropic) {
                // Formato Anthropic: system separado
                StringBuilder json = new StringBuilder();
                json.append("{\"model\":\"").append(escapeJson(model)).append("\"");
                if (systemPrompt != null && !systemPrompt.isBlank()) {
                    json.append(",\"system\":\"").append(escapeJson(systemPrompt)).append("\"");
                }
                json.append(",\"messages\":[");
                json.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}");
                json.append("],\"max_tokens\":1000}");
                body = json.toString();
            } else {
                // Formato OpenAI: system como mensaje
                StringBuilder json = new StringBuilder();
                json.append("{\"model\":\"").append(escapeJson(model)).append("\",\"messages\":[");
                if (systemPrompt != null && !systemPrompt.isBlank()) {
                    json.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"},");
                }
                json.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}],\"max_tokens\":1000}");
                body = json.toString();
            }

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + (isAnthropic ? "messages" : "chat/completions")))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

            if (isAnthropic) {
                requestBuilder.header("x-api-key", apiKey);
                requestBuilder.header("anthropic-version", "2023-06-01");
            } else {
                requestBuilder.header("Authorization", "Bearer " + apiKey);
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("API error " + response.statusCode() + ": " + response.body());
            }
            return extraerContenido(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Error en ServicioIA.chat", e);
        }
    }

    public String chatConHistorial(List<Mensaje> historial, String nuevoMensaje) {
        try {
            boolean isAnthropic = baseUrl.contains("anthropic.com");
            
            // Separar system prompt del resto de mensajes (requerido por Anthropic)
            String systemPrompt = null;
            List<Mensaje> mensajesSinSystem = new java.util.ArrayList<>();
            
            for (Mensaje m : historial) {
                if ("system".equals(m.rol())) {
                    systemPrompt = m.contenido();
                } else {
                    mensajesSinSystem.add(m);
                }
            }
            
            // Construir array de mensajes
            StringBuilder messages = new StringBuilder();
            for (Mensaje m : mensajesSinSystem) {
                if (messages.length() > 0) messages.append(",");
                messages.append("{\"role\":\"").append(m.rol()).append("\",\"content\":\"")
                        .append(escapeJson(m.contenido())).append("\"}");
            }
            
            // Agregar nuevo mensaje del usuario
            if (!nuevoMensaje.isBlank()) {
                if (messages.length() > 0) messages.append(",");
                messages.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(nuevoMensaje)).append("\"}");
            }

            // Construir body según el proveedor
            String body;
            if (isAnthropic) {
                StringBuilder jsonBody = new StringBuilder();
                jsonBody.append("{\"model\":\"").append(escapeJson(model)).append("\"");
                if (systemPrompt != null && !systemPrompt.isBlank()) {
                    jsonBody.append(",\"system\":\"").append(escapeJson(systemPrompt)).append("\"");
                }
                jsonBody.append(",\"messages\":[").append(messages).append("]");
                jsonBody.append(",\"max_tokens\":1000}");
                body = jsonBody.toString();
            } else {
                // OpenAI: incluir system como mensaje normal
                StringBuilder jsonBody = new StringBuilder();
                jsonBody.append("{\"model\":\"").append(escapeJson(model)).append("\",\"messages\":[");
                if (systemPrompt != null && !systemPrompt.isBlank()) {
                    jsonBody.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"},");
                }
                jsonBody.append(messages);
                jsonBody.append("],\"max_tokens\":1000}");
                body = jsonBody.toString();
            }

            // Construir request con headers apropiados
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + (isAnthropic ? "messages" : "chat/completions")))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            
            if (isAnthropic) {
                requestBuilder.header("x-api-key", apiKey);
                requestBuilder.header("anthropic-version", "2023-06-01");
            } else {
                requestBuilder.header("Authorization", "Bearer " + apiKey);
            }
            
            HttpRequest request = requestBuilder.build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new RuntimeException("API error " + response.statusCode() + ": " + response.body());
            }
            return extraerContenido(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Error en ServicioIA.chatConHistorial", e);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String extraerContenido(String json) {
        // Intentar formato Anthropic primero: "content":[{"text":"..."}]
        String anthropicMarker = "\"text\":\"";
        int anthropicStart = json.indexOf(anthropicMarker);
        if (anthropicStart != -1) {
            anthropicStart += anthropicMarker.length();
            int end = anthropicStart;
            while (end < json.length()) {
                char c = json.charAt(end);
                if (c == '\\') end += 2;
                else if (c == '"') break;
                else end++;
            }
            String content = json.substring(anthropicStart, end);
            return unescapeJson(content);
        }
        
        // Formato OpenAI: "content":"..."
        String marker = "\"content\":\"";
        int start = json.indexOf(marker);
        if (start == -1) return json;
        start += marker.length();
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') end += 2;
            else if (c == '"') break;
            else end++;
        }
        String content = json.substring(start, end);
        return unescapeJson(content);
    }
    
    private static String unescapeJson(String s) {
        return s.replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\t", "\t")
                .replace("\\r", "\r");
    }

    public record Mensaje(String rol, String contenido) {
        public static Mensaje usuario(String contenido) { return new Mensaje("user", contenido); }
        public static Mensaje asistente(String contenido) { return new Mensaje("assistant", contenido); }
    }
}
