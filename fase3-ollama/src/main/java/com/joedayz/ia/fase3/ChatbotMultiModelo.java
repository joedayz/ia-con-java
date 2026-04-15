package com.joedayz.ia.fase3;

import com.joedayz.ia.fase3.ollama.ServicioIAOllama;
import com.joedayz.ia.fase3.ollama.ServicioIAOllama.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Chatbot con memoria que permite elegir entre distintos modelos de Ollama.
 *
 * En fase3 el equivalente era ChatbotMultiProveedor (OpenAI/Anthropic/Gemini).
 * Aquí la "multi" es entre modelos locales de Ollama:
 *   llama3.2, mistral, phi3, gemma2, etc.
 *
 * Uso:
 *   # Primer modelo disponible (default)
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiModelo"
 *
 *   # Modelo específico
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiModelo" \
 *     -Dexec.args="mistral"
 */
public class ChatbotMultiModelo {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Chatbot Multi-Modelo con Memoria - Ollama        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        String modeloSolicitado = args.length > 0 ? args[0] : null;

        ServicioIAOllama servicio;
        try {
            servicio = new ServicioIAOllama(modeloSolicitado);
        } catch (Exception e) {
            System.err.println("❌ Error inicializando Ollama: " + e.getMessage());
            System.err.println("   Asegúrate de que Ollama está corriendo: ollama serve");
            return;
        }

        System.out.println("🦙 Modelo activo: " + servicio.getModel());
        System.out.println("📋 Modelos disponibles: " + servicio.listarModelos());
        System.out.println();
        System.out.println("Comandos:");
        System.out.println("  /historial - Ver mensajes guardados");
        System.out.println("  /limpiar   - Borrar historial");
        System.out.println("  /modelo    - Ver modelo actual");
        System.out.println("  salir      - Terminar");
        System.out.println("─".repeat(60));

        Scanner scanner = new Scanner(System.in);
        List<Mensaje> historial = new ArrayList<>();

        historial.add(new Mensaje("system",
                "Eres un asistente amigable y servicial. "
                + "Recuerda el contexto de la conversación y responde de forma concisa."));

        while (true) {
            System.out.print("\n💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                System.out.println("\n👋 Conversación finalizada.");
                System.out.println("📊 Total de mensajes: " + historial.size());
                break;
            }
            if (entrada.isEmpty()) continue;

            if (entrada.equalsIgnoreCase("/modelo")) {
                System.out.println("\n🦙 Modelo actual: " + servicio.getModel());
                continue;
            }
            if (entrada.equalsIgnoreCase("/historial")) {
                mostrarHistorial(historial, servicio.getModel());
                continue;
            }
            if (entrada.equalsIgnoreCase("/limpiar")) {
                historial.clear();
                historial.add(new Mensaje("system", "Eres un asistente amigable y servicial."));
                System.out.println("🧹 Historial limpiado.\n");
                continue;
            }

            try {
                historial.add(Mensaje.usuario(entrada));
                List<Mensaje> paraEnviar = new ArrayList<>(historial);
                String respuesta = enviarConHistorial(servicio, paraEnviar);
                historial.add(Mensaje.asistente(respuesta));
                System.out.println("🤖 " + servicio.getModel() + ": " + respuesta);
                System.out.println("   [Historial: " + historial.size() + " mensajes]");
            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                if (!historial.isEmpty()
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

    private static void mostrarHistorial(List<Mensaje> historial, String modelo) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📋 HISTORIAL | 🦙 Modelo: " + modelo);
        System.out.println("═".repeat(60));
        if (historial.isEmpty()) {
            System.out.println("(vacío)");
        } else {
            for (int i = 0; i < historial.size(); i++) {
                Mensaje msg = historial.get(i);
                String emoji = switch (msg.rol()) {
                    case "system" -> "⚙️"; case "user" -> "💬";
                    case "assistant" -> "🤖"; default -> "❓";
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

