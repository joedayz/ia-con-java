package com.joedayz.ia.fase1;

import com.joedayz.ia.common.config.EnvConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Fase 1: Fundamentos.
 * Primera llamada a una API de IA (OpenAI o compatible).
 * Requiere OPENAI_API_KEY en .env (en la raíz del repo).
 */
public class PrimeraLlamadaOpenAI {

    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    public static void main(String[] args) throws Exception {
        String apiKey = EnvConfig.getOpenAiApiKey();
        String baseUrl = EnvConfig.getOpenAiApiBase();

        String prompt = args.length > 0 ? String.join(" ", args) : "Di 'Hola desde Java' en una frase.";

        String response = enviarChat(baseUrl, apiKey, prompt);
        System.out.println("Respuesta: " + response);
    }

    public static String enviarChat(String baseUrl, String apiKey, String userMessage) throws Exception {
        String url = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";

        String body = """
            {
              "model": "%s",
              "messages": [
                {"role": "user", "content": "%s"}
              ],
              "max_tokens": 500
            }
            """
            .formatted(DEFAULT_MODEL, escapeJson(userMessage));

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error " + response.statusCode() + ": " + response.body());
        }

        return extraerContenido(response.body());
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
}
