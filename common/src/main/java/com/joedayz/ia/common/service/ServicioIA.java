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
        this(EnvConfig.getOpenAiApiBase(), EnvConfig.getOpenAiApiKey(), "gpt-3.5-turbo");
    }

    public ServicioIA(String baseUrl, String apiKey, String model) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public String chat(String userMessage) {
        return chat(null, userMessage);
    }

    public String chat(String systemPrompt, String userMessage) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\"model\":\"").append(escapeJson(model)).append("\",\"messages\":[");
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                json.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"},");
            }
            json.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}],\"max_tokens\":1000}");
            String body = json.toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

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
            StringBuilder messages = new StringBuilder();
            for (Mensaje m : historial) {
                messages.append("{\"role\":\"").append(m.rol()).append("\",\"content\":\"")
                        .append(escapeJson(m.contenido())).append("\"},");
            }
            messages.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(nuevoMensaje)).append("\"}");

            String body = "{\"model\":\"" + escapeJson(model) + "\",\"messages\":[" + messages + "],\"max_tokens\":1000}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

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
        return json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
    }

    public record Mensaje(String rol, String contenido) {
        public static Mensaje usuario(String contenido) { return new Mensaje("user", contenido); }
        public static Mensaje asistente(String contenido) { return new Mensaje("assistant", contenido); }
    }
}
