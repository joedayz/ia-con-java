package com.joedayz.ia.fase3;

import com.joedayz.ia.common.service.ServicioIA;
import com.joedayz.ia.common.service.ServicioIA.Mensaje;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lab 8: Chatbot con memoria persistente (guardada en archivo JSON).
 *
 * Características:
 * - Guarda conversaciones en archivos JSON por sesión
 * - Recupera conversaciones previas al iniciar
 * - Permite múltiples sesiones independientes
 * - Útil para aplicaciones multi-usuario o recuperación después de reiniciar
 *
 * Estructura de archivos:
 * fase3/sessions/
 *   usuario-123.json
 *   session-abc.json
 *   maria.json
 *
 * Cada archivo contiene un array JSON de mensajes.
 */
public class ChatbotConMemoriaPersistente {

    // Directorio donde se guardan las sesiones
    private static final Path SESSIONS_DIR = Paths.get("fase3/sessions");

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Lab 8: Chatbot con Memoria Persistente          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Este chatbot guarda conversaciones en archivos JSON.");
        System.out.println("Puedes retomar conversaciones previas ingresando el mismo ID.");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        // Solicitar ID de sesión
        System.out.print("Ingresa un ID de sesión (ej. usuario123): ");
        String sessionId = scanner.nextLine().trim();

        if (sessionId.isEmpty()) {
            sessionId = "default";
            System.out.println("Sin ID proporcionado, usando: " + sessionId);
        }

        System.out.println("─".repeat(60));

        // Crear directorio de sesiones si no existe
        try {
            Files.createDirectories(SESSIONS_DIR);
        } catch (IOException e) {
            System.err.println("❌ Error creando directorio de sesiones: " + e.getMessage());
            scanner.close();
            return;
        }

        // Cargar historial existente o crear nuevo
        List<Mensaje> historial = cargarSesion(sessionId);

        if (historial.isEmpty()) {
            System.out.println("📝 Nueva sesión creada: " + sessionId);
            // Agregar system prompt inicial
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

        ServicioIA servicio = new ServicioIA();

        while (true) {
            System.out.print("\n💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                guardarSesion(sessionId, historial);
                System.out.println("\n💾 Conversación guardada en: "
                        + obtenerArchivo(sessionId));
                System.out.println("👋 ¡Hasta luego!");
                break;
            }

            if (entrada.isEmpty()) {
                continue;
            }

            // Comando: mostrar historial
            if (entrada.equalsIgnoreCase("/historial")) {
                mostrarHistorial(historial);
                continue;
            }

            // Comando: limpiar historial
            if (entrada.equalsIgnoreCase("/limpiar")) {
                historial.clear();
                historial.add(new Mensaje("system",
                        "Eres un asistente amigable y servicial."));
                guardarSesion(sessionId, historial);
                System.out.println("🧹 Historial limpiado y guardado.\n");
                continue;
            }

            try {
                // Agregar mensaje del usuario
                historial.add(Mensaje.usuario(entrada));

                // Enviar al LLM
                List<Mensaje> paraEnviar = new ArrayList<>(historial);
                String respuesta = enviarConHistorial(servicio, paraEnviar);

                // Agregar respuesta del asistente
                historial.add(Mensaje.asistente(respuesta));

                // Guardar automáticamente después de cada interacción
                guardarSesion(sessionId, historial);

                // Mostrar respuesta
                System.out.println("🤖 Bot: " + respuesta);
                System.out.println("   [Guardado: " + historial.size() + " mensajes]");

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                // Remover último mensaje si falló
                if (historial.size() > 1 && historial.get(historial.size() - 1).rol().equals("user")) {
                    historial.remove(historial.size() - 1);
                }
            }
        }

        scanner.close();
    }

    /**
     * Carga el historial de una sesión desde archivo JSON.
     */
    private static List<Mensaje> cargarSesion(String sessionId) {
        Path archivo = obtenerArchivo(sessionId);

        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }

        try {
            String json = Files.readString(archivo);
            return parsearJson(json);
        } catch (IOException e) {
            System.err.println("⚠️ Error leyendo sesión: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda el historial de una sesión en archivo JSON.
     */
    private static void guardarSesion(String sessionId, List<Mensaje> historial) {
        Path archivo = obtenerArchivo(sessionId);

        try {
            String json = convertirAJson(historial);
            Files.writeString(archivo, json);
        } catch (IOException e) {
            System.err.println("❌ Error guardando sesión: " + e.getMessage());
        }
    }

    /**
     * Obtiene la ruta del archivo de sesión.
     */
    private static Path obtenerArchivo(String sessionId) {
        // Sanitizar el ID (remover caracteres peligrosos)
        String cleanId = sessionId.replaceAll("[^a-zA-Z0-9_-]", "_");
        return SESSIONS_DIR.resolve(cleanId + ".json");
    }

    /**
     * Convierte una lista de mensajes a JSON.
     * Formato: [{"rol":"system","contenido":"..."},{"rol":"user","contenido":"..."}]
     */
    private static String convertirAJson(List<Mensaje> mensajes) {
        StringBuilder json = new StringBuilder("[\n");

        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            json.append("  {")
                    .append("\"rol\":\"").append(escapeJson(m.rol())).append("\",")
                    .append("\"contenido\":\"").append(escapeJson(m.contenido())).append("\"")
                    .append("}");

            if (i < mensajes.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    /**
     * Parsea JSON a lista de mensajes.
     * Implementación simple sin dependencias externas.
     */
    private static List<Mensaje> parsearJson(String json) {
        List<Mensaje> mensajes = new ArrayList<>();

        // Parseo simple (sin librerías)
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) {
            return mensajes;
        }

        json = json.substring(1, json.length() - 1).trim();

        int depth = 0;
        int start = -1;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    String objeto = json.substring(start + 1, i);
                    Mensaje mensaje = parsearObjeto(objeto);
                    if (mensaje != null) {
                        mensajes.add(mensaje);
                    }
                    start = -1;
                }
            }
        }

        return mensajes;
    }

    /**
     * Parsea un objeto JSON individual {"rol":"...","contenido":"..."}.
     */
    private static Mensaje parsearObjeto(String objeto) {
        String rol = extraerValor(objeto, "rol");
        String contenido = extraerValor(objeto, "contenido");

        if (rol == null || contenido == null) {
            return null;
        }

        return new Mensaje(rol, contenido);
    }

    /**
     * Extrae el valor de una clave en un objeto JSON simple.
     */
    private static String extraerValor(String objeto, String clave) {
        String patron = "\"" + clave + "\":\"";
        int inicio = objeto.indexOf(patron);

        if (inicio == -1) {
            return null;
        }

        inicio += patron.length();
        int fin = inicio;

        while (fin < objeto.length()) {
            char c = objeto.charAt(fin);
            if (c == '\\') {
                fin += 2; // saltar carácter escapado
            } else if (c == '"') {
                break;
            } else {
                fin++;
            }
        }

        String valor = objeto.substring(inicio, fin);
        return unescapeJson(valor);
    }

    /**
     * Escapa caracteres especiales para JSON.
     */
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Desescapa caracteres especiales de JSON.
     */
    private static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    /**
     * Envía el historial al servicio de IA.
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
                    case "system" -> "⚙️";
                    case "user" -> "💬";
                    case "assistant" -> "🤖";
                    default -> "❓";
                };

                System.out.printf("%n[%d] %s %s%n", i + 1, emoji, msg.rol().toUpperCase());

                String contenido = msg.contenido();
                if (contenido.length() > 150) {
                    contenido = contenido.substring(0, 147) + "...";
                }
                System.out.println("    " + contenido);
            }
        }

        System.out.println("═".repeat(60) + "\n");
    }
}
