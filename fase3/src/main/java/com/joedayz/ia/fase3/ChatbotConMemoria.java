package com.joedayz.ia.fase3;

import com.joedayz.ia.common.service.ServicioIA;
import com.joedayz.ia.common.service.ServicioIA.Mensaje;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lab 7: Chatbot con memoria conversacional usando List<Mensaje>.
 * 
 * Este chatbot SÍ recuerda el contexto de la conversación completa.
 * Cada interacción se agrega al historial y se envía al LLM.
 * 
 * Estrategia: Buffer Memory (guarda todos los mensajes en memoria)
 * 
 * Ejemplo de interacción:
 * Usuario: "Hola, me llamo Carlos y me gustan los videojuegos"
 * Bot: "Hola Carlos, qué interesante que te gusten los videojuegos..."
 * Usuario: "¿Cuál es mi nombre y qué me gusta?"
 * Bot: "Tu nombre es Carlos y te gustan los videojuegos" ✅
 */
public class ChatbotConMemoria {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Lab 7: Chatbot CON Memoria (Buffer)              ║");
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

        ServicioIA servicio = new ServicioIA();
        Scanner scanner = new Scanner(System.in);

        // ✅ SOLUCIÓN: Historial de mensajes
        List<Mensaje> historial = new ArrayList<>();

        // System prompt inicial (define comportamiento del asistente)
        historial.add(new Mensaje("system",
                "Eres un asistente amigable y servicial. "
                + "Recuerda el contexto de la conversación y responde de forma natural. "
                + "Usa el nombre del usuario si te lo dice."));

        System.out.println("\n✅ Sistema inicializado. Historial: " + historial.size() + " mensaje(s)\n");

        while (true) {
            System.out.print("💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                System.out.println("\n👋 Conversación finalizada.");
                System.out.println("📊 Total de mensajes en historial: " + historial.size());
                break;
            }

            if (entrada.isEmpty()) {
                continue;
            }

            // Comando especial: mostrar historial
            if (entrada.equalsIgnoreCase("/historial")) {
                mostrarHistorial(historial);
                continue;
            }

            // Comando especial: limpiar historial
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
     * Envía el historial completo al servicio de IA.
     * Nota: ServicioIA.chatConHistorial espera historial + nuevo mensaje,
     * pero si ya agregamos el mensaje al historial, pasamos "" como nuevo mensaje.
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
     * Muestra el historial completo de la conversación.
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
