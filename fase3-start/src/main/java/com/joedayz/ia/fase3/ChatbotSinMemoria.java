package com.joedayz.ia.fase3;

import com.joedayz.ia.common.service.ServicioIA;

import java.util.Scanner;

/**
 * Demo del PROBLEMA: Chatbot sin memoria que no recuerda conversaciones previas.
 * 
 * OBJETIVO:
 * Ejecuta este programa y observa el problema. Cada pregunta es tratada
 * independientemente, sin contexto de lo anterior.
 * 
 * PRUEBA:
 * 1. Dile tu nombre: "Me llamo Carlos"
 * 2. Pregunta: "¿Cuál es mi nombre?"
 * 3. Observa que el bot NO lo recuerda ❌
 * 
 * Esto demuestra por qué necesitamos implementar memoria conversacional.
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
        System.out.println("⚠️  PROBLEMA A OBSERVAR:");
        System.out.println("   - Dile tu nombre");
        System.out.println("   - Luego pregúntale cuál es");
        System.out.println("   - Verás que NO lo recuerda");
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
