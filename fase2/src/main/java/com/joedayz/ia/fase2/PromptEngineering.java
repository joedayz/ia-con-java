package com.joedayz.ia.fase2;

import com.joedayz.ia.common.config.EnvConfig;

import java.util.Scanner;

/**
 * Fase 2: Prompt engineering.
 * Interacción con el LLM usando prompts estructurados y un pequeño menú.
 * Soporta: OpenAI, Anthropic (Claude) y Google Gemini.
 * 
 * Uso:
 *   mvn -pl fase2 exec:java
 *   mvn -pl fase2 exec:java -Dexec.args="--provider=anthropic"
 *   mvn -pl fase2 exec:java -Dexec.args="--provider=gemini"
 */
public class PromptEngineering {

    private static final String SYSTEM_PROMPT = """
        Eres un asistente técnico conciso. Respondes en el mismo idioma que el usuario.
        Si te piden código, lo devuelves con sintaxis correcta y comentarios breves.
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

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Fase 2 - Prompt Engineering (Multi-Proveedor)");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Proveedor: " + chat.getProviderName());
        System.out.println();
        System.out.println("Escribe tu pregunta (o 'salir' para terminar):");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = sc.nextLine();
                if (line == null || line.isBlank()) continue;
                if ("salir".equalsIgnoreCase(line.trim())) break;

                String userMessage = line.trim();
                String response = chat.chat(SYSTEM_PROMPT, userMessage);
                System.out.println(response);
                System.out.println();
            }
        }
    }
}
