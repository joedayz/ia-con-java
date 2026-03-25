package com.joedayz.ia.fase2;

import com.joedayz.ia.common.config.EnvConfig;

import java.util.Scanner;

/**
 * Lab Bonus: Chain of Thought (Cadena de Pensamiento).
 * Soporta: OpenAI, Anthropic (Claude) y Google Gemini.
 * 
 * Uso:
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ChainOfThought
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ChainOfThought -Dexec.args="--provider=anthropic"
 *   mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ChainOfThought -Dexec.args="--provider=gemini"
 * 
 * Demuestra cómo pedir al modelo que "piense paso a paso" mejora
 * la calidad de las respuestas, especialmente en razonamiento complejo.
 */
public class ChainOfThought {

    private static final String SYSTEM_PROMPT = """
        Eres un asistente que resuelve problemas paso a paso.
        
        Cuando recibas una pregunta:
        1. Explica tu razonamiento paso a paso
        2. Numera cada paso claramente
        3. Al final, da la respuesta final
        
        Formato de respuesta:
        
        RAZONAMIENTO:
        Paso 1: [primer paso]
        Paso 2: [segundo paso]
        ...
        
        RESPUESTA FINAL: [respuesta]
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
        System.out.println("  CHAIN OF THOUGHT - Razonamiento Paso a Paso");
        System.out.println("=".repeat(60));
        System.out.println("Proveedor: " + chat.getProviderName());
        System.out.println();
        System.out.println("Este asistente muestra su razonamiento completo.");
        System.out.println("Escribe tu pregunta (o 'salir' para terminar)");
        System.out.println();
        
        // Ejemplos de prueba
        System.out.println("💡 Preguntas de prueba sugeridas:");
        System.out.println("   • ¿Cuánto es el 15% de 240?");
        System.out.println("   • Si un tren sale a 60 km/h y otro a 80 km/h en sentido contrario,");
        System.out.println("     separados por 350 km, ¿en cuánto tiempo se encuentran?");
        System.out.println("   • Tengo 3 manzanas. Compro el doble y regalo la mitad. ¿Cuántas tengo?");
        System.out.println("   • ¿Qué día será 100 días después del 24 de marzo de 2026?");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("❓ Tu pregunta: ");
                String line = sc.nextLine();
                
                if (line == null || line.isBlank()) {
                    continue;
                }
                
                if ("salir".equalsIgnoreCase(line.trim())) {
                    System.out.println("\n👋 ¡Hasta pronto!");
                    break;
                }

                String pregunta = line.trim();
                
                // Llamar a la API
                System.out.println("\n🤔 Pensando...\n");
                long inicio = System.currentTimeMillis();
                String respuesta = chat.chat(SYSTEM_PROMPT, pregunta, 1000, 0.3);
                long duracion = System.currentTimeMillis() - inicio;
                
                System.out.println(respuesta);
                System.out.println();
                System.out.println("   ⏱️  Tiempo: " + duracion + "ms");
                System.out.println();
            }
        }
    }
}
