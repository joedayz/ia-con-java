package com.joedayz.ia.fase2.ollama;

import java.util.Scanner;

/**
 * Fase 2 con Ollama: chatbot interactivo con system prompt.
 */
public class PromptEngineering {

    private static final String SYSTEM_PROMPT = """
            Eres un asistente tecnico conciso.
            Respondes en el mismo idioma del usuario.
            Si te piden codigo, incluye ejemplos claros.
            """;

    public static void main(String[] args) throws Exception {
        String model = extraerModelo(args);
        OllamaChat chat = new OllamaChat(model);

        System.out.println("============================================================");
        System.out.println("  Fase 2 Ollama - Prompt Engineering");
        System.out.println("============================================================");
        System.out.println("Base URL: " + chat.getBaseUrl());
        System.out.println("Modelo: " + chat.getModel());
        System.out.println();
        System.out.println("Escribe tu pregunta (o 'salir' para terminar):");
        System.out.println();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = sc.nextLine();
                if (line == null || line.isBlank()) {
                    continue;
                }
                if ("salir".equalsIgnoreCase(line.trim())) {
                    break;
                }

                String response = chat.chat(SYSTEM_PROMPT, line.trim());
                System.out.println(response);
                System.out.println();
            }
        }
    }

    static String extraerModelo(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--model=")) {
                return args[i].substring("--model=".length()).trim();
            }
            if ("--model".equals(args[i]) && i + 1 < args.length) {
                return args[i + 1].trim();
            }
        }
        return null;
    }
}

