package com.joedayz.ia.fase2.ollama;

import java.util.List;

/**
 * Demo comparativa: zero-shot vs few-shot con Ollama.
 */
public class ComparacionZeroShotVsFewShot {

    private static final String ZERO_SHOT_PROMPT = """
            Clasifica el sentimiento del texto como POSITIVO, NEGATIVO o NEUTRO.
            Responde solo con la clasificacion en mayusculas.
            """;

    private static final String FEW_SHOT_PROMPT = """
            Clasifica el sentimiento de textos. Ejemplos:
            Texto: \"Me encanto la pelicula\" -> POSITIVO
            Texto: \"Fue una perdida de tiempo\" -> NEGATIVO
            Texto: \"Estuvo normal\" -> NEUTRO

            Ahora clasifica:
            """;

    public static void main(String[] args) throws Exception {
        String model = PromptEngineering.extraerModelo(args);
        OllamaChat chat = new OllamaChat(model);

        System.out.println("============================================================");
        System.out.println("  Comparacion Zero Shot vs Few Shot (Ollama)");
        System.out.println("============================================================");
        System.out.println("Modelo: " + chat.getModel());
        System.out.println();

        List<String> textos = List.of(
                "Este curso esta genial, aprendi muchisimo",
                "La documentacion es confusa",
                "La reunion fue el martes a las 3 PM",
                "No funciono nada"
        );

        for (String texto : textos) {
            long t1 = System.currentTimeMillis();
            String zero = clasificarZeroShot(chat, texto);
            long d1 = System.currentTimeMillis() - t1;

            long t2 = System.currentTimeMillis();
            String few = clasificarFewShot(chat, texto);
            long d2 = System.currentTimeMillis() - t2;

            System.out.println("Texto: \"" + texto + "\"");
            System.out.println("  Zero-Shot: " + zero + " (" + d1 + "ms)");
            System.out.println("  Few-Shot : " + few + " (" + d2 + "ms)");
            System.out.println();
        }
    }

    static String clasificarZeroShot(OllamaChat chat, String texto) throws Exception {
        return chat.chat(ZERO_SHOT_PROMPT, "Texto: \"" + texto + "\"", 15, 0.0).trim();
    }

    static String clasificarFewShot(OllamaChat chat, String texto) throws Exception {
        return chat.chat(FEW_SHOT_PROMPT, "Texto: \"" + texto + "\"", 15, 0.0).trim();
    }
}

