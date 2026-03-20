package com.joedayz.ia.fase1.ollama;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Fase 1 Ollama: Primera llamada a un modelo local con Ollama.
 * 
 * Ollama permite ejecutar modelos de IA localmente (Mistral, Llama, etc.)
 * sin necesidad de API keys ni costos.
 * 
 * La API es compatible con OpenAI, así que el código es muy similar.
 */
public class PrimeraLlamadaOllama {

    // TODO: Definir constantes
    // private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    // private static final String DEFAULT_MODEL = "mistral";
    // private static final int DEFAULT_PORT = 11434;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Primera Llamada a Ollama (Modelo Local) ===\n");
        
        // TODO: Paso 1 - Verificar que Ollama está corriendo
        // String baseUrl = "http://localhost:11434";
        // if (!isOllamaRunning(baseUrl)) {
        //     System.err.println("❌ Error: Ollama no está corriendo.");
        //     System.err.println("💡 Solución: Ejecuta 'ollama serve' en otra terminal");
        //     System.exit(1);
        // }
        // System.out.println("✅ Ollama está corriendo en " + baseUrl);
        
        // TODO: Paso 2 - Listar modelos disponibles
        // List<String> modelos = listarModelos(baseUrl);
        // System.out.println("📦 Modelos instalados: " + modelos);
        // if (modelos.isEmpty()) {
        //     System.err.println("❌ No hay modelos instalados.");
        //     System.err.println("💡 Solución: Ejecuta 'ollama pull mistral'");
        //     System.exit(1);
        // }
        
        // TODO: Paso 3 - Determinar qué modelo usar
        // String modelo = determinarModelo(args, modelos);
        // System.out.println("🤖 Usando modelo: " + modelo + "\n");
        
        // TODO: Paso 4 - Preparar el prompt
        // String prompt = extraerPrompt(args);
        // System.out.println("Pregunta: " + prompt);
        
        // TODO: Paso 5 - Enviar chat y medir tiempo
        // long inicio = System.currentTimeMillis();
        // String respuesta = enviarChat(baseUrl, modelo, prompt);
        // long duracion = System.currentTimeMillis() - inicio;
        
        // System.out.println("\nRespuesta: " + respuesta);
        // System.out.println("\n⏱️  Tiempo: " + duracion + "ms");
        
        System.out.println("\n🎓 TODO: Implementar los métodos marcados con TODO");
    }

    /**
     * Verifica si Ollama está corriendo.
     * 
     * Hace una request a /api/version para verificar conectividad.
     */
    private static boolean isOllamaRunning(String baseUrl) {
        // TODO: Implementar verificación
        // try {
        //     HttpClient client = HttpClient.newBuilder()
        //         .connectTimeout(Duration.ofSeconds(2))
        //         .build();
        //     
        //     HttpRequest request = HttpRequest.newBuilder()
        //         .uri(URI.create(baseUrl + "/api/version"))
        //         .timeout(Duration.ofSeconds(2))
        //         .GET()
        //         .build();
        //     
        //     HttpResponse<String> response = client.send(request,
        //         HttpResponse.BodyHandlers.ofString());
        //     
        //     return response.statusCode() == 200;
        // } catch (Exception e) {
        //     return false;
        // }
        
        return true; // TODO: Implementar
    }

    /**
     * Lista los modelos instalados en Ollama.
     * 
     * Endpoint: GET /api/tags
     * Response: {"models": [{"name": "mistral:latest"}, ...]}
     */
    private static List<String> listarModelos(String baseUrl) throws Exception {
        // TODO: Implementar listado de modelos
        // HttpClient client = HttpClient.newBuilder().build();
        // 
        // HttpRequest request = HttpRequest.newBuilder()
        //     .uri(URI.create(baseUrl + "/api/tags"))
        //     .GET()
        //     .build();
        // 
        // HttpResponse<String> response = client.send(request,
        //     HttpResponse.BodyHandlers.ofString());
        // 
        // if (response.statusCode() != 200) {
        //     throw new RuntimeException("Error listando modelos: " + response.body());
        // }
        // 
        // // Parsear JSON manualmente (buscar "name":"modelo")
        // List<String> modelos = new ArrayList<>();
        // String json = response.body();
        // Pattern pattern = Pattern.compile("\"name\":\"([^\"]+)\"");
        // Matcher matcher = pattern.matcher(json);
        // while (matcher.find()) {
        //     modelos.add(matcher.group(1));
        // }
        // return modelos;
        
        return List.of(); // TODO: Implementar
    }

    /**
     * Determina qué modelo usar basado en argumentos o modelos disponibles.
     * 
     * Prioridad:
     * 1. --model especificado en args
     * 2. Variable de entorno OLLAMA_MODEL
     * 3. Primer modelo disponible
     */
    private static String determinarModelo(String[] args, List<String> modelosDisponibles) {
        // TODO: Implementar lógica de selección
        // 1. Buscar --model en args
        // for (int i = 0; i < args.length - 1; i++) {
        //     if ("--model".equals(args[i])) {
        //         return args[i + 1];
        //     }
        // }
        // 
        // 2. Buscar variable de entorno
        // String envModel = System.getenv("OLLAMA_MODEL");
        // if (envModel != null && !envModel.isBlank()) {
        //     return envModel;
        // }
        // 
        // 3. Usar primer modelo disponible
        // return modelosDisponibles.get(0);
        
        return "mistral"; // TODO: Implementar
    }

    /**
     * Extrae el prompt de los argumentos (excluyendo flags como --model).
     */
    private static String extraerPrompt(String[] args) {
        // TODO: Implementar extracción de prompt
        // StringBuilder prompt = new StringBuilder();
        // boolean skipNext = false;
        // 
        // for (String arg : args) {
        //     if (skipNext) {
        //         skipNext = false;
        //         continue;
        //     }
        //     if ("--model".equals(arg)) {
        //         skipNext = true;
        //         continue;
        //     }
        //     if (prompt.length() > 0) prompt.append(" ");
        //     prompt.append(arg);
        // }
        // 
        // String result = prompt.toString().trim();
        // return result.isEmpty() ? "Di 'Hola desde Java usando Ollama'" : result;
        
        return "Di 'Hola desde Java usando Ollama'"; // TODO: Implementar
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
        
        // TODO: Construir la URL
        // String url = baseUrl + "/v1/chat/completions";
        
        // TODO: Construir el body JSON
        // String body = """
        //     {
        //       "model": "%s",
        //       "messages": [
        //         {"role": "user", "content": "%s"}
        //       ],
        //       "stream": false
        //     }
        //     """.formatted(model, escapeJson(userMessage));
        
        // TODO: Crear HttpClient
        // HttpClient client = HttpClient.newBuilder()
        //     .connectTimeout(Duration.ofSeconds(10))
        //     .build();
        
        // TODO: Crear HttpRequest
        // NOTA: NO se agrega header "Authorization" para Ollama
        // HttpRequest request = HttpRequest.newBuilder()
        //     .uri(URI.create(url))
        //     .header("Content-Type", "application/json")
        //     .timeout(Duration.ofMinutes(2)) // Modelos locales pueden ser lentos
        //     .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
        //     .build();
        
        // TODO: Enviar y validar
        // HttpResponse<String> response = client.send(request,
        //     HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        //     
        // if (response.statusCode() != 200) {
        //     throw new RuntimeException("API error " + response.statusCode() + ": " + response.body());
        // }
        
        // TODO: Extraer contenido (igual que OpenAI)
        // return extraerContenido(response.body());
        
        return "TODO: Implementar enviarChat"; // TODO: Implementar
    }

    /**
     * Escapa caracteres especiales para JSON.
     * (Reutilizado de fase1-start)
     */
    private static String escapeJson(String s) {
        // TODO: Implementar escape
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
        // TODO: Implementar extracción (igual que en fase1-start)
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
        
        return json.substring(start, end)
            .replace("\\n", "\n")
            .replace("\\\"", "\"");
    }

    /**
     * BONUS: Método para comparar velocidad entre modelos.
     * Útil para mostrar diferencias en clase.
     */
    private static void compararModelos(String baseUrl, List<String> modelos, String prompt) {
        // TODO: Implementar comparación (opcional)
        // System.out.println("\n📊 Comparando modelos...\n");
        // for (String modelo : modelos) {
        //     try {
        //         long inicio = System.currentTimeMillis();
        //         String respuesta = enviarChat(baseUrl, modelo, prompt);
        //         long duracion = System.currentTimeMillis() - inicio;
        //         
        //         System.out.println("Modelo: " + modelo);
        //         System.out.println("Tiempo: " + duracion + "ms");
        //         System.out.println("Respuesta: " + respuesta.substring(0, Math.min(100, respuesta.length())) + "...");
        //         System.out.println();
        //     } catch (Exception e) {
        //         System.err.println("Error con " + modelo + ": " + e.getMessage());
        //     }
        // }
    }
}
