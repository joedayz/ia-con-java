package com.joedayz.ia.fase2;

import com.joedayz.ia.common.config.EnvConfig;

import java.util.Scanner;

/**
 * Lab Bonus: Salida Estructurada en JSON.
 * Soporta: OpenAI, Anthropic (Claude) y Google Gemini.
 * 
 * Uso:
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.SalidaEstructurada
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.SalidaEstructurada -Dexec.args="--provider=anthropic"
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.SalidaEstructurada -Dexec.args="--provider=gemini"
 * 
 * Demuestra cómo solicitar respuestas en formato JSON y parsearlas
 * para usar en aplicaciones Java.
 */
public class SalidaEstructurada {

    private static final String SYSTEM_PROMPT = """
        Eres un analizador de textos que devuelve resultados en formato JSON.
        
        Analiza el texto del usuario y responde ÚNICAMENTE con un JSON válido:
        {
          "sentimiento": "POSITIVO|NEGATIVO|NEUTRO",
          "confianza": 0.0-1.0,
          "palabrasClave": ["palabra1", "palabra2", "palabra3"],
          "razonamiento": "breve explicación"
        }
        
        NO agregues texto adicional, solo el JSON.
        """;

    public static void main(String[] args) throws Exception {
        // Parsear argumentos para proveedor
        String provider = null;
        for (String arg : args) {
            if (arg.startsWith("--provider=")) {
                provider = arg.substring("--provider=".length());
            }
        }
        
        if (provider == null) {
            provider = EnvConfig.get("AI_PROVIDER");
        }
        
        MultiProviderChat chat = provider != null 
            ? new MultiProviderChat(provider) 
            : new MultiProviderChat();

        System.out.println("=".repeat(60));
        System.out.println("  SALIDA ESTRUCTURADA - Análisis en JSON");
        System.out.println("=".repeat(60));
        System.out.println("Proveedor: " + chat.getProviderName());
        System.out.println();
        System.out.println("Este analizador devuelve resultados en formato JSON.");
        System.out.println("Escribe un texto para analizar (o 'salir' para terminar)");
        System.out.println();
        
        // Textos de prueba sugeridos
        System.out.println("💡 Textos de prueba sugeridos:");
        System.out.println("   • Este curso de IA con Java es increíblemente útil");
        System.out.println("   • No me gustó la documentación, está muy confusa");
        System.out.println("   • El proyecto se entregará el próximo viernes");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("📝 Texto a analizar: ");
                String line = sc.nextLine();
                
                if (line == null || line.isBlank()) {
                    continue;
                }
                
                if ("salir".equalsIgnoreCase(line.trim())) {
                    System.out.println("\n👋 ¡Hasta pronto!");
                    break;
                }

                String texto = line.trim();
                
                // Llamar a la API
                long inicio = System.currentTimeMillis();
                String jsonResultado = analizar(chat, texto);
                long duracion = System.currentTimeMillis() - inicio;
                
                // Mostrar resultado formateado
                System.out.println();
                System.out.println("📊 Resultado del análisis:");
                System.out.println();
                
                // Parsear y mostrar de forma bonita
                try {
                    mostrarResultadoFormateado(jsonResultado);
                } catch (Exception e) {
                    System.out.println("JSON crudo (error al parsear):");
                    System.out.println(jsonResultado);
                }
                
                System.out.println();
                System.out.println("   ⏱️  Tiempo: " + duracion + "ms");
                System.out.println();
            }
        }
    }

    /**
     * Llama a la API para analizar el texto y obtener JSON.
     */
    static String analizar(MultiProviderChat chat, String texto) throws Exception {
        return chat.chat(SYSTEM_PROMPT, texto, 300, 0.0);
    }

    /**
     * Parsea y muestra el JSON de forma legible.
     * Nota: En producción usarías Jackson o Gson. Aquí es parsing simple.
     */
    private static void mostrarResultadoFormateado(String json) {
        json = json.trim();
        
        // Extraer campos básicos (parsing simple sin librerías)
        String sentimiento = extraerCampo(json, "sentimiento");
        String confianza = extraerCampo(json, "confianza");
        String palabrasClave = extraerCampo(json, "palabrasClave");
        String razonamiento = extraerCampo(json, "razonamiento");
        
        // Emoji según sentimiento
        String emoji = switch (sentimiento.toUpperCase()) {
            case "POSITIVO" -> "😊";
            case "NEGATIVO" -> "😞";
            case "NEUTRO" -> "😐";
            default -> "🤔";
        };
        
        System.out.println("   " + emoji + " Sentimiento: " + sentimiento);
        System.out.println("   📈 Confianza: " + confianza);
        System.out.println("   🔑 Palabras clave: " + palabrasClave);
        System.out.println("   💭 Razonamiento: " + razonamiento);
        System.out.println();
        System.out.println("   📄 JSON crudo:");
        System.out.println("   " + json);
    }

    /**
     * Extrae un campo de un JSON (parsing básico sin librerías).
     */
    private static String extraerCampo(String json, String campo) {
        String marker = "\"" + campo + "\":";
        int start = json.indexOf(marker);
        if (start == -1) return "N/A";
        
        start += marker.length();
        
        // Skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        // Si es string
        if (json.charAt(start) == '"') {
            start++;
            int end = start;
            while (end < json.length() && json.charAt(end) != '"') {
                if (json.charAt(end) == '\\') end++;
                end++;
            }
            return json.substring(start, end);
        }
        
        // Si es array
        if (json.charAt(start) == '[') {
            int end = start + 1;
            int depth = 1;
            while (end < json.length() && depth > 0) {
                if (json.charAt(end) == '[') depth++;
                if (json.charAt(end) == ']') depth--;
                end++;
            }
            return json.substring(start, end);
        }
        
        // Si es número o booleano
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return json.substring(start, end).trim();
    }
}
