package com.joedayz.ia.fase2.ollama;

import com.joedayz.ia.common.config.EnvConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cliente simple para llamar a Ollama usando el endpoint compatible con OpenAI.
 */
public class OllamaChat {

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "mistral";

    private final String baseUrl;
    private final String model;
    private final HttpClient httpClient;

    public OllamaChat(String requestedModel) throws Exception {
        this.baseUrl = EnvConfig.get("OLLAMA_BASE_URL", DEFAULT_BASE_URL);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        if (!isOllamaRunning()) {
            throw new IllegalStateException(
                    "Ollama no esta corriendo en " + baseUrl + ". Ejecuta: ollama serve");
        }

        List<String> modelos = listarModelos();
        if (modelos.isEmpty()) {
            throw new IllegalStateException(
                    "No hay modelos instalados. Ejecuta: ollama pull mistral");
        }

        this.model = resolverModelo(requestedModel, modelos);
    }

    public OllamaChat() throws Exception {
        this(null);
    }

    public String getModel() {
        return model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String chat(String systemPrompt, String userMessage) throws Exception {
        return chat(systemPrompt, userMessage, 800, 0.7);
    }

    public String chat(String systemPrompt, String userMessage, int maxTokens, double temperature)
            throws Exception {
        String body = construirBody(systemPrompt, userMessage, maxTokens, temperature);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMinutes(2))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ollama API error " + response.statusCode() + ": " + response.body());
        }

        return extraerContenido(response.body());
    }

    public List<String> listarModelos() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/tags"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error listando modelos: " + response.body());
        }

        List<String> modelos = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\\"name\\\":\\\"([^\\\"]+)\\\"").matcher(response.body());
        while (matcher.find()) {
            String name = matcher.group(1);
            modelos.add(name.split(":")[0]);
        }
        return modelos;
    }

    private boolean isOllamaRunning() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/version"))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private String resolverModelo(String requestedModel, List<String> modelosDisponibles) {
        if (requestedModel != null && !requestedModel.isBlank()) {
            if (modelosDisponibles.contains(requestedModel.trim())) {
                return requestedModel.trim();
            }
            System.out.println("[WARN] Modelo " + requestedModel + " no encontrado, se usara uno disponible.");
        }

        String envModel = EnvConfig.get("OLLAMA_MODEL");
        if (envModel != null && modelosDisponibles.contains(envModel)) {
            return envModel;
        }

        if (modelosDisponibles.contains(DEFAULT_MODEL)) {
            return DEFAULT_MODEL;
        }

        return modelosDisponibles.get(0);
    }

    private String construirBody(String systemPrompt, String userMessage, int maxTokens, double temperature) {
        StringBuilder messages = new StringBuilder();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.append(String.format(
                    "{\"role\":\"system\",\"content\":\"%s\"},",
                    escapeJson(systemPrompt)));
        }
        messages.append(String.format(
                "{\"role\":\"user\",\"content\":\"%s\"}",
                escapeJson(userMessage)));

        return String.format("""
                {
                  "model": "%s",
                  "messages": [%s],
                  "max_tokens": %d,
                  "temperature": %.1f,
                  "stream": false
                }
                """, model, messages, maxTokens, temperature);
    }

    private String extraerContenido(String json) {
        String marker = "\"content\"";
        int start = json.indexOf(marker);
        if (start == -1) {
            return json;
        }

        int colonPos = json.indexOf(':', start + marker.length());
        if (colonPos == -1) {
            return json;
        }

        int firstQuote = json.indexOf('"', colonPos);
        if (firstQuote == -1) {
            return json;
        }

        start = firstQuote + 1;
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') {
                end += 2;
            } else if (c == '"') {
                break;
            } else {
                end++;
            }
        }

        return json.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
