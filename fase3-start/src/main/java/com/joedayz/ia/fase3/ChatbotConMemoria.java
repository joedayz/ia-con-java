package com.joedayz.ia.fase3;

import com.joedayz.ia.common.service.ServicioIA;
import com.joedayz.ia.common.service.ServicioIA.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * LAB 7: Chatbot con memoria conversacional usando List<Mensaje>.
 * 
 * OBJETIVO:
 * Implementar un chatbot que SÍ recuerda el contexto de la conversación.
 * 
 * TAREA:
 * Completa los métodos marcados con TODO para:
 * 1. Mantener un historial de mensajes
 * 2. Enviar todo el historial en cada request
 * 3. Agregar comandos especiales (/historial, /limpiar)
 * 
 * CONCEPTOS CLAVE:
 * - Buffer Memory: guarda todos los mensajes
 * - List<Mensaje>: estructura para el historial
 * - chatConHistorial(): método que envía el contexto completo
 */
public class ChatbotConMemoria {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Lab 7: Chatbot CON Memoria (Buffer)              ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // TODO: Inicializar ServicioIA
        ServicioIA servicio = null; // Crear instancia aquí
        
        Scanner scanner = new Scanner(System.in);

        // TODO: Crear el historial de mensajes
        // Pista: usa List<Mensaje> historial = new ArrayList<>();
        List<Mensaje> historial = null;

        // TODO: Agregar el system prompt inicial al historial
        // Pista: historial.add(new Mensaje("system", "Eres un asistente amigable..."));

        System.out.println("Comandos especiales:");
        System.out.println("  /historial - Ver todos los mensajes guardados");
        System.out.println("  /limpiar   - Borrar el historial");
        System.out.println("  salir      - Terminar");
        System.out.println("─".repeat(60));

        while (true) {
            System.out.print("\n💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                System.out.println("\n👋 Conversación finalizada.");
                // TODO: Mostrar total de mensajes en historial
                break;
            }

            if (entrada.isEmpty()) {
                continue;
            }

            // TODO: Implementar comando /historial
            if (entrada.equalsIgnoreCase("/historial")) {
                // Llamar a mostrarHistorial(historial);
                continue;
            }

            // TODO: Implementar comando /limpiar
            if (entrada.equalsIgnoreCase("/limpiar")) {
                // 1. Limpiar el historial
                // 2. Agregar nuevamente el system prompt
                // 3. Informar al usuario
                continue;
            }

            try {
                // TODO: PASO 1 - Agregar el mensaje del usuario al historial
                // Pista: historial.add(Mensaje.usuario(entrada));

                // TODO: PASO 2 - Enviar el historial completo al LLM
                // Pista: usa el método enviarConHistorial() que está abajo

                // TODO: PASO 3 - Agregar la respuesta del asistente al historial
                // Pista: historial.add(Mensaje.asistente(respuesta));

                // TODO: PASO 4 - Mostrar la respuesta
                // System.out.println("🤖 Bot: " + respuesta);

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                // TODO: Si hubo error, remover el último mensaje del usuario
            }
        }

        scanner.close();
    }

    /**
     * TODO: Implementa este método para enviar el historial completo.
     * 
     * PISTAS:
     * 1. Obtén el último mensaje (debe ser del usuario)
     * 2. Obtén el historial previo (todo menos el último)
     * 3. Llama a servicio.chatConHistorial(historialPrevio, ultimoMensaje)
     * 4. Retorna la respuesta
     */
    private static String enviarConHistorial(ServicioIA servicio, List<Mensaje> historial) {
        // TODO: Implementar aquí
        return ""; // Reemplazar con la respuesta real
    }

    /**
     * TODO: Implementa este método para mostrar el historial completo.
     * 
     * FORMATO SUGERIDO:
     * [1] ⚙️ SYSTEM
     *     Eres un asistente amigable...
     * [2] 💬 USER
     *     Hola, me llamo Juan
     * [3] 🤖 ASSISTANT
     *     Hola Juan...
     */
    private static void mostrarHistorial(List<Mensaje> historial) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📋 HISTORIAL DE LA CONVERSACIÓN");
        System.out.println("═".repeat(60));

        // TODO: Recorrer el historial y mostrar cada mensaje
        // Pistas:
        // - Usar un for con índice
        // - Para cada mensaje, mostrar: número, emoji, rol, contenido
        // - Emojis: system=⚙️, user=💬, assistant=🤖

        System.out.println("═".repeat(60) + "\n");
    }
}
