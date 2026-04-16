package com.joedayz.ia.fase4;

import com.joedayz.ia.common.service.ServicioIA;

import java.util.Scanner;

/**
 * Fase 4: RAG simplificado (Retrieval-Augmented Generation).
 * Se proporciona un "documento" de contexto y el modelo responde basándose en él.
 * En producción usarías un vector store y embeddings; aquí simulamos con contexto en el prompt.
 */
public class RAGSimple {

    private static final String CONTEXTO = """
        Documento de referencia (curso IA con Java):
        - Fase 1: Primera llamada a la API de OpenAI desde Java con HttpClient.
        - Fase 2: Prompt engineering con system prompt e interacción por consola.
        - Fase 3: ServicioIA reutilizable (módulo common) con chat(), chat(system, user) y chatConHistorial().
        - Fase 4: RAG simple: preguntas sobre un contexto dado.
        Las API keys se configuran en .env (OPENAI_API_KEY) en la raíz del repo. Copia .env.example a .env.
        """;

    private static final String SYSTEM_RAG = """
        Respondes ÚNICAMENTE usando la información del siguiente documento.
        Si la respuesta no está en el documento, di "No encontré esa información en el documento."
        Sé breve.

        --- DOCUMENTO ---
        %s
        --- FIN DOCUMENTO ---
        """.formatted(CONTEXTO);

    public static void main(String[] args) {
        ServicioIA servicio = new ServicioIA();
        System.out.println("Fase 4 - RAG simple. Pregunta sobre el curso (o 'salir'):");

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String pregunta = sc.nextLine();
                if (pregunta == null || pregunta.isBlank()) continue;
                if ("salir".equalsIgnoreCase(pregunta.trim())) break;

                String respuesta = servicio.chat(SYSTEM_RAG, pregunta.trim());
                System.out.println(respuesta);
                System.out.println();
            }
        }
    }
}
