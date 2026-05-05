package com.joedayz.ia.fase3;

import com.joedayz.ia.common.config.EnvConfig;
import com.joedayz.ia.common.service.ServicioIA;
import com.joedayz.ia.common.service.ServicioIA.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Demo: Chatbot con memoria que funciona con múltiples proveedores:
 * - OpenAI (GPT-3.5-turbo, GPT-4)
 * - Anthropic (Claude)
 * - Google Gemini
 * 
 * Uso:
 *   # OpenAI (default)
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor"
 * 
 *   # Anthropic
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor" \
 *     -Dexec.args="anthropic"
 * 
 *   # Gemini
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor" \
 *     -Dexec.args="gemini"
 */
public class ChatbotMultiProveedor {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Chatbot Multi-Proveedor con Memoria              ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // Seleccionar proveedor (default: openai)
        String proveedor = args.length > 0 ? args[0].toLowerCase() : "openai";
        
        ServicioIA servicio;
        String nombreProveedor;

        try {
            switch (proveedor) {
                case "anthropic", "claude" -> {
                    if (!EnvConfig.hasKey("ANTHROPIC_API_KEY")) {
                        System.err.println("❌ ANTHROPIC_API_KEY no configurada");
                        System.err.println("   Configura: export ANTHROPIC_API_KEY=sk-ant-tu-clave");
                        return;
                    }
                    servicio = new ServicioIA(
                        EnvConfig.getAnthropicApiBase(),
                        EnvConfig.getAnthropicApiKey(),
                        "claude-haiku-4-5"
                    );
                    nombreProveedor = "Anthropic Claude 3 Haiku";
                }
                case "gemini", "google" -> {
                    if (!EnvConfig.hasKey("GEMINI_API_KEY")) {
                        System.err.println("❌ GEMINI_API_KEY no configurada");
                        System.err.println("   Configura: export GEMINI_API_KEY=tu-gemini-key");
                        return;
                    }
                    servicio = new ServicioIA(
                        EnvConfig.getGeminiApiBase(),
                        EnvConfig.getGeminiApiKey(),
                        "gemini-pro"
                    );
                    nombreProveedor = "Google Gemini Pro";
                }
                default -> {
                    servicio = new ServicioIA(); // OpenAI default
                    nombreProveedor = "OpenAI GPT-3.5-turbo";
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error inicializando proveedor: " + e.getMessage());
            return;
        }

        System.out.println("🤖 Proveedor: " + nombreProveedor);
        System.out.println("─".repeat(60));
        System.out.println();
        System.out.println("Comandos:");
        System.out.println("  /historial - Ver mensajes guardados");
        System.out.println("  /limpiar   - Borrar historial");
        System.out.println("  /proveedor - Ver proveedor actual");
        System.out.println("  salir      - Terminar");
        System.out.println("─".repeat(60));

        Scanner scanner = new Scanner(System.in);
        List<Mensaje> historial = new ArrayList<>();

        // System prompt inicial
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

            if (entrada.isEmpty()) {
                continue;
            }

            // Comando: ver proveedor
            if (entrada.equalsIgnoreCase("/proveedor")) {
                System.out.println("\n🤖 Proveedor actual: " + nombreProveedor);
                continue;
            }

            // Comando: historial
            if (entrada.equalsIgnoreCase("/historial")) {
                mostrarHistorial(historial, nombreProveedor);
                continue;
            }

            // Comando: limpiar
            if (entrada.equalsIgnoreCase("/limpiar")) {
                historial.clear();
                historial.add(new Mensaje("system",
                        "Eres un asistente amigable y servicial."));
                System.out.println("🧹 Historial limpiado.\n");
                continue;
            }

            try {
                // Agregar mensaje del usuario
                historial.add(Mensaje.usuario(entrada));

                // Enviar con historial completo
                List<Mensaje> paraEnviar = new ArrayList<>(historial);
                String respuesta = enviarConHistorial(servicio, paraEnviar);

                // Agregar respuesta del asistente
                historial.add(Mensaje.asistente(respuesta));

                // Mostrar respuesta
                System.out.println("🤖 " + nombreProveedor + ": " + respuesta);
                System.out.println("   [Historial: " + historial.size() + " mensajes]");

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                // Remover último mensaje si falló
                if (historial.size() > 0 && 
                    historial.get(historial.size() - 1).rol().equals("user")) {
                    historial.remove(historial.size() - 1);
                }
            }
        }

        scanner.close();
    }

    /**
     * Envía el historial completo al servicio de IA.
     */
    private static String enviarConHistorial(ServicioIA servicio, List<Mensaje> historial) {
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
     * Muestra el historial de la conversación.
     */
    private static void mostrarHistorial(List<Mensaje> historial, String proveedor) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📋 HISTORIAL DE LA CONVERSACIÓN");
        System.out.println("🤖 Proveedor: " + proveedor);
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
                if (contenido.length() > 100) {
                    contenido = contenido.substring(0, 97) + "...";
                }
                System.out.println("    " + contenido);
            }
        }

        System.out.println("═".repeat(60) + "\n");
    }
}
