package com.joedayz.ia.fase2;

import com.joedayz.ia.common.config.EnvConfig;

import java.util.List;

/**
 * Demo comparativa: Zero-Shot vs Few-Shot Learning.
 * Soporta: OpenAI, Anthropic (Claude) y Google Gemini.
 * 
 * Uso:
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ComparacionZeroShotVsFewShot
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ComparacionZeroShotVsFewShot -Dexec.args="--provider=anthropic"
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ComparacionZeroShotVsFewShot -Dexec.args="--provider=gemini"
 * 
 * Ejecuta el mismo texto con ambas técnicas para mostrar
 * cómo los ejemplos mejoran la precisión y consistencia.
 */
public class ComparacionZeroShotVsFewShot {

    // Zero-shot: solo instrucciones, sin ejemplos
    private static final String ZERO_SHOT_PROMPT = """
        Clasifica el sentimiento del siguiente texto como POSITIVO, NEGATIVO o NEUTRO.
        Responde solo con la clasificación en mayúsculas.
        """;

    // Few-shot: instrucciones + ejemplos
    private static final String FEW_SHOT_PROMPT = """
        Clasifica el sentimiento de textos. Ejemplos:
        
        Texto: "Me encantó la película" → POSITIVO
        Texto: "Fue una pérdida de tiempo" → NEGATIVO
        Texto: "Estuvo normal" → NEUTRO
        Texto: "¡Increíble servicio!" → POSITIVO
        Texto: "La peor experiencia" → NEGATIVO
        Texto: "El producto mide 15cm" → NEUTRO
        
        Ahora clasifica:
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

        System.out.println("=".repeat(70));
        System.out.println("  COMPARACIÓN: Zero-Shot vs Few-Shot Learning");
        System.out.println("=".repeat(70));
        System.out.println("Proveedor: " + chat.getProviderName());
        System.out.println();
        
        // Textos de prueba variados
        List<String> textosPrueba = List.of(
            "Este curso está genial, aprendí muchísimo",
            "La documentación es confusa y difícil de seguir",
            "La reunión fue el martes a las 3:00 PM",
            "¡Excelente profesor! Sus explicaciones son clarísimas",
            "No funcionó nada, perdí el tiempo",
            "El proyecto tiene 5 módulos en total"
        );

        for (String texto : textosPrueba) {
            System.out.println("📝 Texto: \"" + texto + "\"");
            System.out.println();
            
            // Zero-shot
            long inicio1 = System.currentTimeMillis();
            String zeroShot = clasificarZeroShot(chat, texto);
            long tiempo1 = System.currentTimeMillis() - inicio1;
            
            // Few-shot
            long inicio2 = System.currentTimeMillis();
            String fewShot = clasificarFewShot(chat, texto);
            long tiempo2 = System.currentTimeMillis() - inicio2;
            
            // Comparar resultados
            String emoji1 = getEmoji(zeroShot);
            String emoji2 = getEmoji(fewShot);
            boolean coinciden = zeroShot.equalsIgnoreCase(fewShot);
            
            System.out.println("   Zero-Shot:  " + emoji1 + " " + zeroShot + " (" + tiempo1 + "ms)");
            System.out.println("   Few-Shot:   " + emoji2 + " " + fewShot + " (" + tiempo2 + "ms)");
            
            if (coinciden) {
                System.out.println("   ✅ Ambas técnicas coinciden");
            } else {
                System.out.println("   ⚠️  Resultados diferentes - Few-Shot suele ser más confiable");
            }
            
            System.out.println();
            System.out.println("-".repeat(70));
            System.out.println();
        }
    }

    static String clasificarZeroShot(MultiProviderChat chat, String texto) throws Exception {
        String userMessage = "Texto: \"" + texto + "\"";
        return chat.chat(ZERO_SHOT_PROMPT, userMessage, 10, 0.0).trim();
    }

    static String clasificarFewShot(MultiProviderChat chat, String texto) throws Exception {
        String userMessage = "Texto: \"" + texto + "\"";
        return chat.chat(FEW_SHOT_PROMPT, userMessage, 10, 0.0).trim();
    }

    private static String getEmoji(String sentimiento) {
        return switch (sentimiento.toUpperCase()) {
            case "POSITIVO" -> "😊";
            case "NEGATIVO" -> "😞";
            case "NEUTRO" -> "😐";
            default -> "🤔";
        };
    }
}
