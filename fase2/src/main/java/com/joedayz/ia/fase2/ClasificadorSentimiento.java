package com.joedayz.ia.fase2;

import com.joedayz.ia.common.config.EnvConfig;

import java.util.Scanner;

/**
 * Lab 6: Clasificador de Sentimientos usando Few-Shot Learning.
 * Soporta: OpenAI, Anthropic (Claude) y Google Gemini.
 * 
 * Uso:
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento -Dexec.args="--provider=anthropic"
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento -Dexec.args="--provider=gemini"
 * 
 * Demuestra cómo incluir ejemplos en el prompt mejora la precisión
 * y garantiza un formato de salida consistente.
 */
public class ClasificadorSentimiento {

    // Few-shot prompt: incluye ejemplos para guiar al modelo
    private static final String SYSTEM_PROMPT = """
        Eres un clasificador de sentimientos experto.
        
        Tu tarea es clasificar textos en una de estas categorías:
        - POSITIVO: El texto expresa satisfacción, alegría, o aprobación
        - NEGATIVO: El texto expresa insatisfacción, tristeza, o desaprobación
        - NEUTRO: El texto es informativo o no expresa emoción clara
        
        IMPORTANTE:
        1. Responde SOLO con una palabra: POSITIVO, NEGATIVO o NEUTRO
        2. No agregues explicaciones ni puntuación adicional
        3. Analiza el tono general del mensaje
        
        EJEMPLOS:
        
        Texto: "El producto es excelente y superó mis expectativas"
        Clasificación: POSITIVO
        
        Texto: "No funcionó como esperaba, muy decepcionante"
        Clasificación: NEGATIVO
        
        Texto: "El paquete llegó el martes por la tarde"
        Clasificación: NEUTRO
        
        Texto: "¡Increíble servicio! Totalmente recomendado"
        Clasificación: POSITIVO
        
        Texto: "La peor experiencia que he tenido"
        Clasificación: NEGATIVO
        
        Texto: "El evento se realizará el próximo mes"
        Clasificación: NEUTRO
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
        System.out.println("  CLASIFICADOR DE SENTIMIENTOS - Few-Shot Learning");
        System.out.println("=".repeat(60));
        System.out.println("Proveedor: " + chat.getProviderName());
        System.out.println();
        System.out.println("Este clasificador usa 6 ejemplos para entrenar al modelo.");
        System.out.println("Escribe un texto para clasificar (o 'salir' para terminar)");
        System.out.println();
        
        // Textos de prueba sugeridos
        System.out.println("💡 Textos de prueba sugeridos:");
        System.out.println("   • Me encanta este curso de IA con Java");
        System.out.println("   • El servicio al cliente fue horrible");
        System.out.println("   • El curso empieza a las 7:00 PM");
        System.out.println("   • ¡Excelente explicación del profesor!");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("📝 Texto a clasificar: ");
                String line = sc.nextLine();
                
                if (line == null || line.isBlank()) {
                    continue;
                }
                
                if ("salir".equalsIgnoreCase(line.trim())) {
                    System.out.println("\n👋 ¡Hasta pronto!");
                    break;
                }

                String userMessage = line.trim();
                
                // Llamar a la API
                long inicio = System.currentTimeMillis();
                String clasificacion = clasificar(chat, userMessage);
                long duracion = System.currentTimeMillis() - inicio;
                
                // Mostrar resultado con emoji
                String emoji = switch (clasificacion.toUpperCase()) {
                    case "POSITIVO" -> "😊";
                    case "NEGATIVO" -> "😞";
                    case "NEUTRO" -> "😐";
                    default -> "❓";
                };
                
                System.out.println("\n" + emoji + "  " + clasificacion.toUpperCase() + " (" + duracion + "ms)\n");
            }
        }
    }

    /**
     * Clasifica el texto usando el modelo con ejemplos (few-shot)
     */
    static String clasificar(MultiProviderChat chat, String texto) throws Exception {
        // Construir el prompt con el texto del usuario
        String userPrompt = String.format("""
            Texto: "%s"
            Clasificación:""", texto);
        
        return chat.chat(SYSTEM_PROMPT, userPrompt, 100, 0.0).trim();
    }
}
