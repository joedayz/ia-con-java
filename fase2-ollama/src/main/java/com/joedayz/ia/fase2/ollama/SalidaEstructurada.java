package com.joedayz.ia.fase2.ollama;

import java.util.Scanner;

/**
 * Bonus: salida JSON estructurada usando Ollama.
 */
public class SalidaEstructurada {

    private static final String SYSTEM_PROMPT = """
            Analiza el texto y responde solo con JSON valido:
            {
              "sentimiento": "POSITIVO|NEGATIVO|NEUTRO",
              "confianza": 0.0,
              "palabrasClave": ["palabra1", "palabra2"],
              "razonamiento": "explicacion breve"
            }
            """;

    public static void main(String[] args) throws Exception {
        String model = PromptEngineering.extraerModelo(args);
        OllamaChat chat = new OllamaChat(model);

        System.out.println("============================================================");
        System.out.println("  Salida Estructurada JSON (Ollama)");
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
                String json = chat.chat(SYSTEM_PROMPT, line.trim(), 350, 0.0);
                long tiempo = System.currentTimeMillis() - inicio;

                System.out.println();
                System.out.println(json);
                System.out.println("Tiempo: " + tiempo + "ms");
                System.out.println();
            }
        }
    }
}

