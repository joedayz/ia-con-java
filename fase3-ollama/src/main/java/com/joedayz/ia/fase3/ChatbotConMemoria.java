package com.joedayz.ia.fase3;

import com.joedayz.ia.fase3.ollama.ServicioIAOllama;
import com.joedayz.ia.fase3.ollama.ServicioIAOllama.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lab 7: Chatbot con memoria conversacional usando List<Mensaje> — versión Ollama.
 *
 * Este chatbot SÍ recuerda el contexto de la conversación completa.
 * Cada interacción se agrega al historial y se envía al LLM.
 *
 * Estrategia: Buffer Memory (guarda todos los mensajes en RAM).
 *
 * Ejecutar:
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoria"
 */
public class ChatbotConMemoria {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Lab 7: Chatbot CON Memoria (Buffer) - Ollama     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Este chatbot SÍ recuerda toda la conversación.");
        System.out.println("El historial completo se envía en cada request.");
        System.out.println();
        System.out.println("Comandos especiales:");
        System.out.println("  /historial - Ver todos los mensajes guardados");
        System.out.println("  /limpiar   - Borrar el historial y empezar de nuevo");
        System.out.println("  salir      - Terminar el programa");
        System.out.println("─".repeat(60));

        ServicioIAOllama servicio = new ServicioIAOllama();
        Scanner scanner = new Scanner(System.in);

        // ✅ SOLUCIÓN: Historial de mensajes
        List<Mensaje> historial = new ArrayList<>();

        // System prompt inicial
        historial.add(new Mensaje("system",
                "Eres un asistente amigable y servicial. "
                + "Recuerda el contexto de la conversación y responde de forma natural. "
                + "Usa el nombre del usuario si te lo dice."));

        System.out.println("\n✅ Modelo: " + servicio.getModel());
        System.out.println("✅ Sistema inicializado. Historial: " + historial.size() + " mensaje(s)\n");

        while (true) {
            System.out.print("💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                System.out.println("\n👋 Conversación finalizada.");
                System.out.println("📊 Total de mensajes en historial: " + historial.size());
                break;
            }
            if (entrada.isEmpty()) continue;

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

                // 2. Enviar TODO el historial al LLM
                List<Mensaje> paraEnviar = new ArrayList<>(historial);
                String respuesta = enviarConHistorial(servicio, paraEnviar);

                // 3. Agregar respuesta del asistente al historial
                historial.add(Mensaje.asistente(respuesta));

                // 4. Mostrar respuesta
                System.out.println("🤖 Bot: " + respuesta);
                System.out.println("   [Historial: " + historial.size() + " mensajes]");

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                if (historial.size() > 1
                        && historial.get(historial.size() - 1).rol().equals("user")) {
                    historial.remove(historial.size() - 1);
                }
            }
        }

        scanner.close();
    }

    private static String enviarConHistorial(ServicioIAOllama servicio, List<Mensaje> historial) {
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
                String contenido = msg.contenido();
                if (contenido.length() > 100) contenido = contenido.substring(0, 97) + "...";
                System.out.println("    " + contenido);
            }
        }
        System.out.println("═".repeat(60) + "\n");
    }
}
