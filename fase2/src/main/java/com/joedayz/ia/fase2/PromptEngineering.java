package com.joedayz.ia.fase2;

import com.joedayz.ia.common.config.EnvConfig;

import java.util.Scanner;

/**
 * Fase 2: Prompt engineering.
 * Interacción con el LLM usando prompts estructurados y un pequeño menú.
 */
public class PromptEngineering {

    private static final String SYSTEM_PROMPT = """
        Eres un asistente técnico conciso. Respondes en el mismo idioma que el usuario.
        Si te piden código, lo devuelves con sintaxis correcta y comentarios breves.
        """;

    public static void main(String[] args) throws Exception {
        String apiKey = EnvConfig.getOpenAiApiKey();
        String baseUrl = EnvConfig.getOpenAiApiBase();

        System.out.println("Fase 2 - Prompt Engineering. Escribe tu pregunta (o 'salir'):");

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = sc.nextLine();
                if (line == null || line.isBlank()) continue;
                if ("salir".equalsIgnoreCase(line.trim())) break;

                String userMessage = line.trim();
                String response = llamarConSystemPrompt(baseUrl, apiKey, userMessage);
                System.out.println(response);
                System.out.println();
            }
        }
    }

    static String llamarConSystemPrompt(String baseUrl, String apiKey, String userMessage) throws Exception {
        String url = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";
        String body = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [
                {"role": "system", "content": "%s"},
                {"role": "user", "content": "%s"}
              ],
              "max_tokens": 800
            }
            """
            .formatted(escapeJson(SYSTEM_PROMPT), escapeJson(userMessage));

        java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(15))
                .build();

        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofSeconds(60))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        java.net.http.HttpResponse<String> response = client.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));

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
