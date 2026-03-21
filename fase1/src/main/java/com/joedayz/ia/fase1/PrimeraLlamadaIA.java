package com.joedayz.ia.fase1;

import com.joedayz.ia.common.config.EnvConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Fase 1: Primera llamada a APIs de IA con soporte multi-proveedor.
 * Soporta: OpenAI (GPT-3.5/GPT-4), Anthropic (Claude) y Google Gemini.
 * 
 * Uso:
 *   mvn -pl fase1 exec:java -Dexec.args="Hola"
 *   mvn -pl fase1 exec:java -Dexec.args="--provider=anthropic Hola mundo"
 *   mvn -pl fase1 exec:java -Dexec.args="--provider=gemini Explica Java"
 * 
 * Variables de entorno:
 *   OPENAI_API_KEY     - Obligatoria para OpenAI
 *   ANTHROPIC_API_KEY  - Obligatoria para Anthropic
 *   GEMINI_API_KEY     - Obligatoria para Google Gemini
 *   AI_PROVIDER        - "openai", "anthropic" o "gemini" (opcional, autodetecta)
 */
public class PrimeraLlamadaIA {

    private static final String OPENAI_MODEL = "gpt-3.5-turbo";
    private static final String ANTHROPIC_MODEL = "claude-3-haiku-20240307";
    private static final String GEMINI_MODEL = "gemini-2.5-flash";

    public static void main(String[] args) throws Exception {
        // Parsear argumentos
        String provider = null;
        StringBuilder promptBuilder = new StringBuilder();

        for (String arg : args) {
            if (arg.startsWith("--provider=")) {
                provider = arg.substring("--provider=".length());
            } else {
                if (promptBuilder.length() > 0) promptBuilder.append(" ");
                promptBuilder.append(arg);
            }
        }

        String prompt = promptBuilder.length() > 0 
            ? promptBuilder.toString() 
            : "Di 'Hola desde Java' en una frase.";

        // Autodetectar proveedor si no se especificó
        if (provider == null) {
            provider = EnvConfig.get("AI_PROVIDER", detectarProveedor());
        }

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("🤖 Primera Llamada a IA - Multi-Proveedor");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Proveedor: " + provider.toUpperCase());
        System.out.println("Prompt: " + prompt);
        System.out.println("───────────────────────────────────────────────────────");

        String respuesta;
        if ("anthropic".equalsIgnoreCase(provider)) {
            respuesta = chatAnthropic(prompt);
        } else if ("openai".equalsIgnoreCase(provider)) {
            respuesta = chatOpenAI(prompt);
        } else if ("gemini".equalsIgnoreCase(provider)) {
            respuesta = chatGemini(prompt);
        } else {
            throw new IllegalArgumentException("Proveedor desconocido: " + provider + 
                ". Usa 'openai', 'anthropic' o 'gemini'");
        }

        System.out.println("\n💬 Respuesta:");
        System.out.println(respuesta);
        System.out.println("═══════════════════════════════════════════════════════");
    }

    /**
     * Autodetecta el proveedor basándose en qué API keys están disponibles.
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
                "No se encontró ninguna API key. Configura OPENAI_API_KEY, GEMINI_API_KEY o ANTHROPIC_API_KEY");
        }
    }

    // ═══════════════════════════════════════════════════════
    // OPENAI
    // ═══════════════════════════════════════════════════════

    private static String chatOpenAI(String userMessage) throws Exception {
        String apiKey = EnvConfig.getOpenAiApiKey();
        String baseUrl = EnvConfig.getOpenAiApiBase();
        String url = baseUrl.endsWith("/") 
            ? baseUrl + "chat/completions" 
            : baseUrl + "/chat/completions";

        String body = """
            {
              "model": "%s",
              "messages": [
                {"role": "user", "content": "%s"}
              ],
              "max_tokens": 500
            }
            """.formatted(OPENAI_MODEL, escapeJson(userMessage));

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

        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API error " + response.statusCode() + 
                ": " + response.body());
        }

        return extraerContenidoOpenAI(response.body());
    }

    private static String extraerContenidoOpenAI(String json) {
        // Busca: "content": "texto" o "content":"texto" (con o sin espacios)
        String marker = "\"content\"";
        int start = json.indexOf(marker);
        if (start == -1) return json;
        
        // Buscar el : después de "content"
        int colonPos = json.indexOf(':', start + marker.length());
        if (colonPos == -1) return json;
        
        // Buscar la primera comilla después del :
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

    private static String chatAnthropic(String userMessage) throws Exception {
        String apiKey = EnvConfig.getAnthropicApiKey();
        String baseUrl = EnvConfig.getAnthropicApiBase();
        String url = baseUrl.endsWith("/") 
            ? baseUrl + "messages" 
            : baseUrl + "/messages";

        // Anthropic usa un formato diferente
        String body = """
            {
              "model": "%s",
              "messages": [
                {"role": "user", "content": "%s"}
              ],
              "max_tokens": 500
            }
            """.formatted(ANTHROPIC_MODEL, escapeJson(userMessage));

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)  // Anthropic usa x-api-key, no Bearer
                .header("anthropic-version", "2023-06-01")  // Header requerido
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Anthropic API error " + response.statusCode() + 
                ": " + response.body());
        }

        return extraerContenidoAnthropic(response.body());
    }

    private static String extraerContenidoAnthropic(String json) {
        // Anthropic responde: {"content":[{"type":"text","text":"respuesta"}]}
        // Buscar "text": "..." de forma más robusta
        String marker = "\"text\"";
        int start = json.indexOf(marker);
        if (start == -1) return json;
        
        // Buscar el : después de "text"
        int colonPos = json.indexOf(':', start + marker.length());
        if (colonPos == -1) return json;
        
        // Buscar la primera comilla después del :
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

    private static String chatGemini(String userMessage) throws Exception {
        String apiKey = EnvConfig.getGeminiApiKey();
        String baseUrl = EnvConfig.getGeminiApiBase();
        String url = baseUrl.endsWith("/")
            ? baseUrl + "models/" + GEMINI_MODEL + ":generateContent?key=" + apiKey
            : baseUrl + "/models/" + GEMINI_MODEL + ":generateContent?key=" + apiKey;

        // Gemini usa un formato diferente
        String body = """
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }]
            }
            """.formatted(escapeJson(userMessage));

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API error " + response.statusCode() +
                ": " + response.body());
        }

        return extraerContenidoGemini(response.body());
    }

    private static String extraerContenidoGemini(String json) {
        // Gemini responde: {"candidates":[{"content":{"parts":[{"text":"respuesta"}]}}]}
        // Buscar "text": "..." dentro de parts
        String marker = "\"text\"";
        int start = json.indexOf(marker);
        if (start == -1) return json;

        // Buscar el : después de "text"
        int colonPos = json.indexOf(':', start + marker.length());
        if (colonPos == -1) return json;

        // Buscar la primera comilla después del :
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

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
