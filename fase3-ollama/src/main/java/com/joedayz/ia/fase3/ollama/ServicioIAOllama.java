package com.joedayz.ia.fase3.ollama;

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
 * Equivalente a ServicioIA del módulo common, pero para Ollama local.
 *
 * NO requiere API keys. Usa la API compatible con OpenAI de Ollama:
 *   http://localhost:11434/v1/chat/completions
 *
 * Prerequisitos:
 *   1. Instalar Ollama: https://ollama.ai
 *   2. Descargar un modelo: ollama pull llama3.2
 *   3. Ollama debe estar corriendo: ollama serve
 *
 * Variables de entorno opcionales (.env):
 *   OLLAMA_BASE_URL=http://localhost:11434   (default)
 *   OLLAMA_MODEL=llama3.2                    (default, o el primer modelo disponible)
 */
public class ServicioIAOllama {

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL    = "llama3.2";

    private final String    baseUrl;
    private final String    model;
    private final HttpClient httpClient;

    // ──────────────────────────────────────────────────────────────────────────
    // Constructores
    // ──────────────────────────────────────────────────────────────────────────

    /** Constructor por defecto: lee OLLAMA_BASE_URL y OLLAMA_MODEL del .env */
    public ServicioIAOllama() {
        this(null);
    }

    /** Constructor con modelo específico (se busca entre los disponibles) */
    public ServicioIAOllama(String modeloSolicitado) {
        this.baseUrl = EnvConfig.get("OLLAMA_BASE_URL", DEFAULT_BASE_URL);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        if (!isOllamaRunning()) {
            throw new IllegalStateException(
                "Ollama no está corriendo en " + this.baseUrl + "\n" +
                "  Solución: ollama serve");
        }

        List<String> disponibles = listarModelos();
        if (disponibles.isEmpty()) {
            throw new IllegalStateException(
                "No hay modelos instalados en Ollama.\n" +
                "  Solución: ollama pull llama3.2");
        }

        this.model = resolverModelo(modeloSolicitado, disponibles);
        System.out.println("🦙 ServicioIAOllama listo → modelo: " + this.model
                + " | url: " + this.baseUrl);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // API pública (misma interfaz que ServicioIA del módulo common)
    // ──────────────────────────────────────────────────────────────────────────

    public String chat(String userMessage) {
        return chat(null, userMessage);
    }

    /**
     * Chat simple sin historial previo.
     * Equivale a ServicioIA.chat(systemPrompt, userMessage)
     */
    public String chat(String systemPrompt, String userMessage) {
        List<Mensaje> mensajes = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            mensajes.add(new Mensaje("system", systemPrompt));
        }
        mensajes.add(new Mensaje("user", userMessage));
        return llamarOllama(mensajes);
    }

    /**
     * Chat con historial completo (memoria conversacional).
     * Equivale a ServicioIA.chatConHistorial(historial, nuevoMensaje)
     *
     * @param historial    lista de mensajes previos (system, user, assistant...)
     * @param nuevoMensaje nuevo mensaje del usuario
     * @return respuesta del modelo
     */
    public String chatConHistorial(List<Mensaje> historial, String nuevoMensaje) {
        List<Mensaje> mensajes = new ArrayList<>(historial);
        if (!nuevoMensaje.isBlank()) {
            mensajes.add(new Mensaje("user", nuevoMensaje));
        }
        return llamarOllama(mensajes);
    }

    public String getModel()   { return model;   }
    public String getBaseUrl() { return baseUrl; }

    // ──────────────────────────────────────────────────────────────────────────
    // Diagnóstico
    // ──────────────────────────────────────────────────────────────────────────

    public boolean isOllamaRunning() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/version"))
                    .timeout(Duration.ofSeconds(3))
                    .GET().build();
            return httpClient.send(req,
                    HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> listarModelos() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/tags"))
                    .timeout(Duration.ofSeconds(10))
                    .GET().build();
            HttpResponse<String> resp = httpClient.send(req,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() != 200) return List.of();
            List<String> modelos = new ArrayList<>();
            Matcher m = Pattern.compile("\"name\":\"([^\"]+)\"").matcher(resp.body());
            while (m.find()) modelos.add(m.group(1).split(":")[0]);
            return modelos;
        } catch (Exception e) {
            return List.of();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Lógica interna
    // ──────────────────────────────────────────────────────────────────────────

    private String llamarOllama(List<Mensaje> mensajes) {
        try {
            String body = construirBody(mensajes);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(3))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = httpClient.send(req,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() != 200) {
                throw new RuntimeException("Ollama error " + resp.statusCode()
                        + ": " + resp.body());
            }
            return extraerContenido(resp.body());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error llamando a Ollama: " + e.getMessage(), e);
        }
    }

    private String construirBody(List<Mensaje> mensajes) {
        StringBuilder msgs = new StringBuilder();
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            if (i > 0) msgs.append(",");
            msgs.append("{\"role\":\"").append(m.rol())
                .append("\",\"content\":\"").append(escapeJson(m.contenido()))
                .append("\"}");
        }
        return "{\"model\":\"" + escapeJson(model) + "\","
             + "\"messages\":[" + msgs + "],"
             + "\"stream\":false}";
    }

    private String resolverModelo(String solicitado, List<String> disponibles) {
        if (solicitado != null && !solicitado.isBlank()
                && disponibles.contains(solicitado.trim())) {
            return solicitado.trim();
        }
        String envModel = EnvConfig.get("OLLAMA_MODEL");
        if (envModel != null && disponibles.contains(envModel)) return envModel;
        if (disponibles.contains(DEFAULT_MODEL)) return DEFAULT_MODEL;
        return disponibles.get(0);
    }

    private String extraerContenido(String json) {
        // Formato OpenAI: choices[0].message.content
        String marker = "\"content\"";
        int idx = json.indexOf(marker);
        if (idx == -1) return json;
        int colon = json.indexOf(':', idx + marker.length());
        if (colon == -1) return json;
        int q1 = json.indexOf('"', colon + 1);
        if (q1 == -1) return json;
        int start = q1 + 1, end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') { end += 2; }
            else if (c == '"') { break; }
            else end++;
        }
        return unescapeJson(json.substring(start, end));
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\\\", "\u0000").replace("\\n", "\n")
                .replace("\\r", "\r").replace("\\t", "\t")
                .replace("\\\"", "\"").replace("\u0000", "\\");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Record Mensaje (misma interfaz que ServicioIA.Mensaje)
    // ──────────────────────────────────────────────────────────────────────────

    public record Mensaje(String rol, String contenido) {
        public static Mensaje usuario(String contenido)   { return new Mensaje("user", contenido); }
        public static Mensaje asistente(String contenido) { return new Mensaje("assistant", contenido); }
    }
}

