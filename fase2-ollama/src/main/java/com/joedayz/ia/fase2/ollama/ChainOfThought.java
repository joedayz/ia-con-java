package com.joedayz.ia.fase2.ollama;

import java.util.Scanner;

/**
 * Bonus: razonamiento paso a paso con Ollama.
 */
public class ChainOfThought {

    private static final String SYSTEM_PROMPT = """
            Resuelve problemas paso a paso.

            Formato:
            RAZONAMIENTO:
            Paso 1: ...
            Paso 2: ...

            RESPUESTA FINAL: ...
            """;

    public static void main(String[] args) throws Exception {
        String model = PromptEngineering.extraerModelo(args);
        OllamaChat chat = new OllamaChat(model);

        System.out.println("============================================================");
        System.out.println("  Chain of Thought (Ollama)");
        System.out.println("============================================================");
        System.out.println("Modelo: " + chat.getModel());
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("Pregunta (o 'salir'): ");
                String line = sc.nextLine();
                if (line == null || line.isBlank()) {
                    continue;
                }
                if ("salir".equalsIgnoreCase(line.trim())) {
                    break;
                }

                long inicio = System.currentTimeMillis();
                String response = chat.chat(SYSTEM_PROMPT, line.trim(), 900, 0.3);
                long tiempo = System.currentTimeMillis() - inicio;

                System.out.println();
                System.out.println(response);
                System.out.println();
                System.out.println("Tiempo: " + tiempo + "ms");
                System.out.println();
            }
        }
    }
}
