package com.joedayz.ia.fase1.ollama;

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
 * Fase 1 Ollama: Primera llamada a un modelo local con Ollama.
 * 
 * Ollama permite ejecutar modelos de IA localmente (Mistral, Llama, etc.)
 * sin necesidad de API keys ni costos.
 * 
 * La API es compatible con OpenAI, así que el código es muy similar.
 * 
 * Uso:
 *   mvn clean compile exec:java
 *   mvn exec:java -Dexec.args="Explica qué es un record en Java"
 *   mvn exec:java -Dexec.args="--model llama3.2 Hola mundo"
 */
public class PrimeraLlamadaOllama {

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final int DEFAULT_PORT = 11434;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Primera Llamada a Ollama (Modelo Local) ===\n");
        
        // Paso 1: Verificar que Ollama está corriendo
        String baseUrl = System.getenv("OLLAMA_BASE_URL");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = DEFAULT_BASE_URL;
        }
        
        if (!isOllamaRunning(baseUrl)) {
            System.err.println("❌ Error: Ollama no está corriendo.");
            System.err.println("💡 Solución:");
            System.err.println("   1. Instala Ollama: https://ollama.com");
            System.err.println("   2. Ejecuta en otra terminal: ollama serve");
            System.err.println("   3. Verifica con: curl " + baseUrl + "/api/version");
            System.exit(1);
        }
        System.out.println("✅ Ollama está corriendo en " + baseUrl);
        
        // Paso 2: Listar modelos disponibles
        List<String> modelos = listarModelos(baseUrl);
        System.out.println("📦 Modelos instalados: " + modelos);
        
        if (modelos.isEmpty()) {
            System.err.println("❌ No hay modelos instalados.");
            System.err.println("💡 Solución: Ejecuta uno de estos comandos:");
            System.err.println("   ollama pull mistral       # 4.1 GB - Recomendado");
            System.err.println("   ollama pull llama3.2      # 2 GB - Más rápido");
            System.err.println("   ollama pull phi3          # 2.3 GB - Para laptops modestas");
            System.exit(1);
        }
        
        // Paso 3: Determinar qué modelo usar
        String modelo = determinarModelo(args, modelos);
        System.out.println("🤖 Usando modelo: " + modelo + "\n");
        
        // Paso 4: Preparar el prompt
        String prompt = extraerPrompt(args);
        System.out.println("Pregunta: " + prompt);
        
        // Paso 5: Enviar chat y medir tiempo
        long inicio = System.currentTimeMillis();
        String respuesta = enviarChat(baseUrl, modelo, prompt);
        long duracion = System.currentTimeMillis() - inicio;
        
        System.out.println("\nRespuesta: " + respuesta);
        System.out.println("\n⏱️  Tiempo: " + duracion + "ms (" + (duracion / 1000.0) + " segundos)");
        
        // Comparación de velocidad (opcional)
        if (modelos.size() > 1 && args.length > 0 && "--compare".equals(args[0])) {
            compararModelos(baseUrl, modelos, prompt);
        }
    }

    /**
     * Verifica si Ollama está corriendo.
     * 
     * Hace una request a /api/version para verificar conectividad.
     */
    private static boolean isOllamaRunning(String baseUrl) {
        try {
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/version"))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lista los modelos instalados en Ollama.
     * 
     * Endpoint: GET /api/tags
     * Response: {"models": [{"name": "mistral:latest"}, ...]}
     */
    private static List<String> listarModelos(String baseUrl) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/tags"))
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Error listando modelos: " + response.body());
        }
        
        // Parsear JSON manualmente (buscar "name":"modelo")
        List<String> modelos = new ArrayList<>();
        String json = response.body();
        Pattern pattern = Pattern.compile("\"name\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String nombreCompleto = matcher.group(1);
            // Extraer solo el nombre base (quitar :latest, :7b, etc.)
            String nombreBase = nombreCompleto.split(":")[0];
            modelos.add(nombreBase);
        }
        return modelos;
    }

    /**
     * Determina qué modelo usar basado en argumentos o modelos disponibles.
     * 
     * Prioridad:
     * 1. --model especificado en args
     * 2. Variable de entorno OLLAMA_MODEL
     * 3. Preferir mistral si está disponible
     * 4. Primer modelo disponible
     */
    private static String determinarModelo(String[] args, List<String> modelosDisponibles) {
        // 1. Buscar --model en args
        for (int i = 0; i < args.length - 1; i++) {
            if ("--model".equals(args[i])) {
                String modeloEspecificado = args[i + 1];
                // Verificar si está disponible
                if (modelosDisponibles.contains(modeloEspecificado)) {
                    return modeloEspecificado;
                } else {
                    System.err.println("⚠️  Modelo '" + modeloEspecificado + 
                        "' no encontrado. Usando modelo disponible.");
                }
            }
        }
        
        // 2. Buscar variable de entorno
        String envModel = System.getenv("OLLAMA_MODEL");
        if (envModel != null && !envModel.isBlank() && modelosDisponibles.contains(envModel)) {
            return envModel;
        }
        
        // 3. Preferir mistral si está disponible (es el más recomendado)
        if (modelosDisponibles.contains("mistral")) {
            return "mistral";
        }
        
        // 4. Usar primer modelo disponible
        return modelosDisponibles.get(0);
    }

    /**
     * Extrae el prompt de los argumentos (excluyendo flags como --model).
     */
    private static String extraerPrompt(String[] args) {
        StringBuilder prompt = new StringBuilder();
        boolean skipNext = false;
        
        for (String arg : args) {
            if (skipNext) {
                skipNext = false;
                continue;
            }
            if ("--model".equals(arg) || "--compare".equals(arg)) {
                if ("--model".equals(arg)) {
                    skipNext = true;
                }
                continue;
            }
            if (prompt.length() > 0) prompt.append(" ");
            prompt.append(arg);
        }
        
        String result = prompt.toString().trim();
        return result.isEmpty() ? "Di 'Hola desde Java usando Ollama'" : result;
    }

    /**
     * Envía un chat a Ollama.
     * 
     * Endpoint: POST /v1/chat/completions (compatible con OpenAI)
     * NO REQUIERE API KEY 🎉
     * 
     * Body:
     * {
     *   "model": "mistral",
     *   "messages": [{"role": "user", "content": "..."}],
     *   "stream": false
     * }
     */
    private static String enviarChat(String baseUrl, String model, String userMessage) 
            throws Exception {
        
        // Construir la URL
        String url = baseUrl + "/v1/chat/completions";
        
        // Construir el body JSON
        String body = """
            {
              "model": "%s",
              "messages": [
                {"role": "user", "content": "%s"}
              ],
              "stream": false
            }
            """.formatted(model, escapeJson(userMessage));
        
        // Crear HttpClient con timeouts generosos (modelos locales pueden ser lentos)
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        // Crear HttpRequest
        // NOTA IMPORTANTE: NO se agrega header "Authorization" para Ollama
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofMinutes(2)) // Modelos locales pueden tardar
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build();
        
        // Enviar y validar
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            
        if (response.statusCode() != 200) {
            throw new RuntimeException("API error " + response.statusCode() + ": " + response.body());
        }
        
        // Extraer contenido (igual que OpenAI)
        return extraerContenido(response.body());
    }

    /**
     * Escapa caracteres especiales para JSON.
     * (Reutilizado de fase1-start)
     */
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Extrae el contenido de la respuesta JSON.
     * El formato es idéntico a OpenAI:
     * {
     *   "choices": [{"message": {"content": "..."}}]
     * }
     */
    private static String extraerContenido(String json) {
        String marker = "\"content\":\"";
        int start = json.indexOf(marker);
        if (start == -1) {
            // Si no encuentra el formato esperado, devolver el JSON completo
            return json;
        }
        start += marker.length();
        
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') {
                end += 2; // Saltar carácter escapado
            } else if (c == '"') {
                break; // Fin del contenido
            } else {
                end++;
            }
        }
        
        // Extraer y decodificar secuencias de escape
        return json.substring(start, end)
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }

    /**
     * BONUS: Método para comparar velocidad entre modelos.
     * Útil para mostrar diferencias en clase.
     * 
     * Uso: mvn exec:java -Dexec.args="--compare ¿Qué es Java?"
     */
    private static void compararModelos(String baseUrl, List<String> modelos, String prompt) {
        System.out.println("\n📊 Comparando modelos con el mismo prompt...\n");
        System.out.println("=" .repeat(80));
        
        for (String modelo : modelos) {
            try {
                System.out.print("\n🤖 Modelo: " + modelo + "... ");
                
                long inicio = System.currentTimeMillis();
                String respuesta = enviarChat(baseUrl, modelo, prompt);
                long duracion = System.currentTimeMillis() - inicio;
                
                System.out.println("✅ (" + duracion + "ms)");
                System.out.println("-".repeat(80));
                
                // Mostrar solo los primeros 200 caracteres de la respuesta
                String respuestaCorta = respuesta.length() > 200 
                    ? respuesta.substring(0, 200) + "..." 
                    : respuesta;
                System.out.println(respuestaCorta);
                System.out.println("-".repeat(80));
                
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
    }
}
