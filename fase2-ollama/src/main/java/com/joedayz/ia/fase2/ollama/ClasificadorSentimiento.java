package com.joedayz.ia.fase2.ollama;

import java.util.Scanner;

/**
 * Lab 6: Clasificador de sentimientos usando few-shot con Ollama.
 */
public class ClasificadorSentimiento {

    private static final String SYSTEM_PROMPT = """
            Eres un clasificador de sentimientos experto.

            Clasifica textos en una categoria:
            - POSITIVO
            - NEGATIVO
            - NEUTRO

            Reglas:
            1. Responde solo con POSITIVO, NEGATIVO o NEUTRO.
            2. No agregues explicaciones.

            Ejemplos:
            Texto: \"Me encanto la pelicula\" -> POSITIVO
            Texto: \"Fue una perdida de tiempo\" -> NEGATIVO
            Texto: \"El paquete llego el martes\" -> NEUTRO
            """;

    public static void main(String[] args) throws Exception {
        String model = PromptEngineering.extraerModelo(args);
        OllamaChat chat = new OllamaChat(model);

        System.out.println("============================================================");
        System.out.println("  Clasificador de Sentimientos - Few Shot (Ollama)");
        System.out.println("============================================================");
        System.out.println("Modelo: " + chat.getModel());
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("Texto (o 'salir'): ");
                String line = sc.nextLine();
                if (line == null || line.isBlank()) {
                    continue;
                }
                if ("salir".equalsIgnoreCase(line.trim())) {
                    break;
                }

                long inicio = System.currentTimeMillis();
                String result = clasificar(chat, line.trim());
                long tiempo = System.currentTimeMillis() - inicio;

                System.out.println("Clasificacion: " + result.toUpperCase() + " (" + tiempo + "ms)");
                System.out.println();
            }
        }
    }

    static String clasificar(OllamaChat chat, String texto) throws Exception {
        String userPrompt = String.format("Texto: \"%s\"\nClasificacion:", texto);
        return chat.chat(SYSTEM_PROMPT, userPrompt, 60, 0.0).trim();
    }
}

