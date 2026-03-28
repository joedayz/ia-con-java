package com.joedayz.ia.fase3;

import com.joedayz.ia.common.service.ServicioIA;

import java.util.Scanner;

/**
 * Demo del PROBLEMA: Chatbot sin memoria que no recuerda conversaciones previas.
 * 
 * Cada pregunta es tratada independientemente, sin contexto de lo anterior.
 * Esto demuestra por qué necesitamos implementar memoria conversacional.
 * 
 * Ejemplo de interacción:
 * Usuario: "Hola, me llamo Carlos"
 * Bot: "Hola Carlos, ¿en qué puedo ayudarte?"
 * Usuario: "¿Cuál es mi nombre?"
 * Bot: "Lo siento, no tengo esa información" ❌
 */
public class ChatbotSinMemoria {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   DEMO: Chatbot SIN Memoria (Problema)             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Este chatbot NO recuerda conversaciones anteriores.");
        System.out.println("Cada pregunta es tratada de forma independiente.");
        System.out.println();
        System.out.println("Intenta decirle tu nombre y luego pregúntale cuál es...");
        System.out.println();
        System.out.println("Escribe 'salir' para terminar.");
        System.out.println("─".repeat(60));

        ServicioIA servicio = new ServicioIA();
        Scanner scanner = new Scanner(System.in);

        String systemPrompt = "Eres un asistente amigable. "
                + "Responde de forma concisa y profesional.";

        while (true) {
            System.out.print("\n💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                System.out.println("\n👋 ¡Hasta luego!");
                break;
            }

            if (entrada.isEmpty()) {
                continue;
            }

            try {
                // ❌ PROBLEMA: Cada llamada es independiente, sin contexto previo
                String respuesta = servicio.chat(systemPrompt, entrada);
                System.out.println("🤖 Bot: " + respuesta);

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
