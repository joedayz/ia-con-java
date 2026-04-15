package com.joedayz.ia.fase3;

import com.joedayz.ia.fase3.ollama.ServicioIAOllama;
import com.joedayz.ia.fase3.ollama.ServicioIAOllama.Mensaje;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lab 8: Chatbot con memoria persistente (guardada en archivo JSON) — versión Ollama.
 *
 * Características:
 * - Guarda conversaciones en archivos JSON por sesión
 * - Recupera conversaciones previas al iniciar
 * - Permite múltiples sesiones independientes
 *
 * Estructura de archivos:
 *   fase3/sessions/
 *     usuario-123.json
 *     session-abc.json
 *
 * Ejecutar:
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"
 */
public class ChatbotConMemoriaPersistente {

    private static final Path SESSIONS_DIR = Paths.get("fase3/sessions");

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Lab 8: Chatbot con Memoria Persistente - Ollama  ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Este chatbot guarda conversaciones en archivos JSON.");
        System.out.println("Puedes retomar conversaciones previas con el mismo ID.");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa un ID de sesión (ej. usuario123): ");
        String sessionId = scanner.nextLine().trim();
        if (sessionId.isEmpty()) {
            sessionId = "default";
            System.out.println("Sin ID proporcionado, usando: " + sessionId);
        }
        System.out.println("─".repeat(60));

        try {
            Files.createDirectories(SESSIONS_DIR);
        } catch (IOException e) {
            System.err.println("❌ Error creando directorio de sesiones: " + e.getMessage());
            scanner.close();
            return;
        }

        List<Mensaje> historial = cargarSesion(sessionId);
        if (historial.isEmpty()) {
            System.out.println("📝 Nueva sesión creada: " + sessionId);
            historial.add(new Mensaje("system",
                    "Eres un asistente amigable y servicial. "
                    + "Recuerda el contexto de la conversación y responde de forma natural."));
        } else {
            System.out.println("📂 Sesión cargada: " + sessionId
                    + " (" + (historial.size() - 1) + " mensajes previos)");
        }

        System.out.println();
        System.out.println("Comandos especiales:");
        System.out.println("  /historial - Ver mensajes guardados");
        System.out.println("  /limpiar   - Borrar historial de esta sesión");
        System.out.println("  salir      - Guardar y terminar");
        System.out.println("─".repeat(60));

        ServicioIAOllama servicio = new ServicioIAOllama();
        System.out.println("🦙 Modelo: " + servicio.getModel() + "\n");

        while (true) {
            System.out.print("\n💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                guardarSesion(sessionId, historial);
                System.out.println("\n💾 Conversación guardada en: " + obtenerArchivo(sessionId));
                System.out.println("👋 ¡Hasta luego!");
                break;
            }
            if (entrada.isEmpty()) continue;

            if (entrada.equalsIgnoreCase("/historial")) {
                mostrarHistorial(historial);
                continue;
            }

            if (entrada.equalsIgnoreCase("/limpiar")) {
                historial.clear();
                historial.add(new Mensaje("system", "Eres un asistente amigable y servicial."));
                guardarSesion(sessionId, historial);
                System.out.println("🧹 Historial limpiado y guardado.\n");
                continue;
            }

            try {
                historial.add(Mensaje.usuario(entrada));
                List<Mensaje> paraEnviar = new ArrayList<>(historial);
                String respuesta = enviarConHistorial(servicio, paraEnviar);
                historial.add(Mensaje.asistente(respuesta));
                guardarSesion(sessionId, historial);
                System.out.println("🤖 Bot: " + respuesta);
                System.out.println("   [Guardado: " + historial.size() + " mensajes]");
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

    // ── Persistencia JSON ──────────────────────────────────────────────────────

    private static List<Mensaje> cargarSesion(String sessionId) {
        Path archivo = obtenerArchivo(sessionId);
        if (!Files.exists(archivo)) return new ArrayList<>();
        try {
            return parsearJson(Files.readString(archivo));
        } catch (IOException e) {
            System.err.println("⚠️ Error leyendo sesión: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void guardarSesion(String sessionId, List<Mensaje> historial) {
        try {
            Files.writeString(obtenerArchivo(sessionId), convertirAJson(historial));
        } catch (IOException e) {
            System.err.println("❌ Error guardando sesión: " + e.getMessage());
        }
    }

    private static Path obtenerArchivo(String sessionId) {
        return SESSIONS_DIR.resolve(sessionId.replaceAll("[^a-zA-Z0-9_-]", "_") + ".json");
    }

    private static String convertirAJson(List<Mensaje> mensajes) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            json.append("  {")
                .append("\"rol\":\"").append(escapeJson(m.rol())).append("\",")
                .append("\"contenido\":\"").append(escapeJson(m.contenido())).append("\"}");
            if (i < mensajes.size() - 1) json.append(",");
            json.append("\n");
        }
        return json.append("]").toString();
    }

    private static List<Mensaje> parsearJson(String json) {
        List<Mensaje> mensajes = new ArrayList<>();
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) return mensajes;
        json = json.substring(1, json.length() - 1).trim();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    Mensaje m = parsearObjeto(json.substring(start + 1, i));
                    if (m != null) mensajes.add(m);
                    start = -1;
                }
            }
        }
        return mensajes;
    }

    private static Mensaje parsearObjeto(String obj) {
        String rol = extraerValor(obj, "rol");
        String contenido = extraerValor(obj, "contenido");
        return (rol != null && contenido != null) ? new Mensaje(rol, contenido) : null;
    }

    private static String extraerValor(String obj, String clave) {
        String patron = "\"" + clave + "\":\"";
        int inicio = obj.indexOf(patron);
        if (inicio == -1) return null;
        inicio += patron.length();
        int fin = inicio;
        while (fin < obj.length()) {
            char c = obj.charAt(fin);
            if (c == '\\') fin += 2;
            else if (c == '"') break;
            else fin++;
        }
        return unescapeJson(obj.substring(inicio, fin));
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\r", "\r")
                .replace("\\t", "\t").replace("\\\"", "\"").replace("\\\\", "\\");
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
        System.out.println("📋 HISTORIAL DE LA SESIÓN");
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
                if (contenido.length() > 150) contenido = contenido.substring(0, 147) + "...";
                System.out.println("    " + contenido);
            }
        }
        System.out.println("═".repeat(60) + "\n");
    }
}
