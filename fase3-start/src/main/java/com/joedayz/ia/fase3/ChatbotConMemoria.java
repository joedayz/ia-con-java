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

        ServicioIA servicio = new ServicioIA(); // Crear instancia aquí
        
        Scanner scanner = new Scanner(System.in);

        List<Mensaje> historial = new ArrayList<>();

        // System prompt inicial (define comportamiento del asistente)
        historial.add(new Mensaje("system",
                "Eres un asistente amigable y servicial. "
                        + "Recuerda el contexto de la conversación y responde de forma natural. "
                        + "Usa el nombre del usuario si te lo dice."));

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
                System.out.println("📊 Total de mensajes en historial: " + historial.size());
                break;
            }

            if (entrada.isEmpty()) {
                continue;
            }


            if (entrada.equalsIgnoreCase("/historial")) {
                mostrarHistorial(historial);
                continue;
            }


            if (entrada.equalsIgnoreCase("/limpiar")) {
                historial.clear();
                historial.add(new Mensaje("system",
                        "Eres un asistente amigable y servicial. "
                                + "Recuerda el contexto de la conversación."));
                System.out.println("🧹 Historial limpiado. Empezando conversación nueva.\n");
                continue;
            }

            try {
                // 1. Agregar mensaje del usuario al historial
                historial.add(Mensaje.usuario(entrada));

                // 2. Enviar el historial al LLM
                // Nota: chatConHistorial espera el historial completo + el nuevo mensaje
                // En este caso, ya agregamos el mensaje al historial, así que pasamos lista vacía
                List<Mensaje> paraEnviar = new ArrayList<>(historial);

                // Llamada al servicio (envía el historial completo)
                String respuesta = enviarConHistorial(servicio, paraEnviar);

                // 3. Agregar respuesta del asistente al historial
                historial.add(Mensaje.asistente(respuesta));

                // 4. Mostrar respuesta
                System.out.println("🤖 Bot: " + respuesta);
                System.out.println("   [Historial: " + historial.size() + " mensajes]");

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                // Remover el último mensaje del usuario si falló
                if (historial.size() > 1 && historial.get(historial.size() - 1).rol().equals("user")) {
                    historial.remove(historial.size() - 1);
                }
            }
        }

        scanner.close();
    }

    /**
     *
     * 
     * PISTAS:
     * 1. Obtén el último mensaje (debe ser del usuario)
     * 2. Obtén el historial previo (todo menos el último)
     * 3. Llama a servicio.chatConHistorial(historialPrevio, ultimoMensaje)
     * 4. Retorna la respuesta
     */
    private static String enviarConHistorial(ServicioIA servicio, List<Mensaje> historial) {
        // Extraer el último mensaje de usuario
        String ultimoMensaje = "";
        List<Mensaje> historialPrevio = historial;
        if (!historial.isEmpty()) {
            Mensaje ultimo = historial.get(historial.size() - 1);
            if ("user".equals(ultimo.rol())) {
                historialPrevio = historial.subList(0, historial.size() - 1);
                ultimoMensaje = ultimo.contenido();
            }
        }
        return servicio.chatConHistorial(historialPrevio, ultimoMensaje);
    }

    /**
     *
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

        if (historial.isEmpty()) {
            System.out.println("(vacío)");
        } else {
            for (int i = 0; i < historial.size(); i++) {
                Mensaje msg = historial.get(i);
                String emoji = switch (msg.rol()) {
                    case "system" -> "⚙️";
                    case "user" -> "💬";
                    case "assistant" -> "🤖";
                    default -> "❓";
                };

                System.out.printf("%n[%d] %s %s%n", i + 1, emoji, msg.rol().toUpperCase());

                // Limitar contenido largo
                String contenido = msg.contenido();
                if (contenido.length() > 100) {
                    contenido = contenido.substring(0, 97) + "...";
                }
                System.out.println("    " + contenido);
            }
        }

        System.out.println("═".repeat(60) + "\n");
    }
}
