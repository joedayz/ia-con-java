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
import java.util.stream.Stream;

/**
 * RETO: Gestor de múltiples sesiones de chat.
 * 
 * Permite manejar conversaciones de múltiples usuarios simultáneamente.
 * Cada usuario tiene su propio historial independiente.
 * 
 * Casos de uso:
 * - Aplicaciones multi-usuario (varios clientes conectados)
 * - Chatbots en producción
 * - Pruebas con diferentes personalidades/contextos
 * 
 * Comandos:
 * - /nueva [sessionId]  : Crear o cambiar a una sesión
 * - /listar            : Ver todas las sesiones disponibles
 * - /info              : Ver información de la sesión actual
 * - /borrar [sessionId]: Eliminar una sesión
 * - salir              : Terminar
 */
public class GestorMultiSesion {

    private static final Path SESSIONS_DIR = Paths.get("fase3/sessions");
    private static String sessionIdActual = null;
    private static List<Mensaje> historialActual = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   RETO: Gestor de Múltiples Sesiones de Chat          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Gestiona conversaciones de múltiples usuarios.");
        System.out.println("Cada sesión mantiene su propio historial independiente.");
        System.out.println();
        System.out.println("Comandos:");
        System.out.println("  /nueva [id]   - Crear/cambiar sesión");
        System.out.println("  /listar       - Ver todas las sesiones");
        System.out.println("  /info         - Info de sesión actual");
        System.out.println("  /borrar [id]  - Eliminar sesión");
        System.out.println("  /historial    - Ver mensajes de sesión actual");
        System.out.println("  salir         - Terminar");
        System.out.println("═".repeat(60));

        // Crear directorio de sesiones
        try {
            Files.createDirectories(SESSIONS_DIR);
        } catch (IOException e) {
            System.err.println("❌ Error creando directorio: " + e.getMessage());
            return;
        }

        ServicioIA servicio = new ServicioIA();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Mostrar sesión actual
            if (sessionIdActual != null) {
                System.out.print("\n[" + sessionIdActual + "] 💬 Tú: ");
            } else {
                System.out.print("\n[sin sesión] ⚠️  Comando: ");
            }

            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                if (sessionIdActual != null) {
                    guardarSesion(sessionIdActual, historialActual);
                    System.out.println("💾 Sesión guardada: " + sessionIdActual);
                }
                System.out.println("👋 ¡Hasta luego!");
                break;
            }

            if (entrada.isEmpty()) {
                continue;
            }

            // Comando: /nueva [sessionId]
            if (entrada.toLowerCase().startsWith("/nueva")) {
                String[] partes = entrada.split("\\s+", 2);
                if (partes.length < 2) {
                    System.out.println("⚠️ Uso: /nueva <sessionId>");
                    continue;
                }
                cambiarSesion(partes[1].trim());
                continue;
            }

            // Comando: /listar
            if (entrada.equalsIgnoreCase("/listar")) {
                listarSesiones();
                continue;
            }

            // Comando: /info
            if (entrada.equalsIgnoreCase("/info")) {
                mostrarInfo();
                continue;
            }

            // Comando: /borrar [sessionId]
            if (entrada.toLowerCase().startsWith("/borrar")) {
                String[] partes = entrada.split("\\s+", 2);
                if (partes.length < 2) {
                    System.out.println("⚠️ Uso: /borrar <sessionId>");
                    continue;
                }
                borrarSesion(partes[1].trim());
                continue;
            }

            // Comando: /historial
            if (entrada.equalsIgnoreCase("/historial")) {
                if (sessionIdActual == null) {
                    System.out.println("⚠️ No hay sesión activa. Usa /nueva <id>");
                } else {
                    mostrarHistorial(historialActual);
                }
                continue;
            }

            // Validar que hay sesión activa
            if (sessionIdActual == null) {
                System.out.println("⚠️ Primero crea o selecciona una sesión con: /nueva <id>");
                continue;
            }

            // Chat normal
            try {
                historialActual.add(Mensaje.usuario(entrada));

                List<Mensaje> paraEnviar = new ArrayList<>(historialActual);
                String respuesta = enviarConHistorial(servicio, paraEnviar);

                historialActual.add(Mensaje.asistente(respuesta));
                guardarSesion(sessionIdActual, historialActual);

                System.out.println("🤖 Bot: " + respuesta);
                System.out.println("   💾 [Guardado: " + historialActual.size() + " mensajes]");

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                if (historialActual.size() > 0 && 
                    historialActual.get(historialActual.size() - 1).rol().equals("user")) {
                    historialActual.remove(historialActual.size() - 1);
                }
            }
        }

        scanner.close();
    }

    /**
     * Cambia a una sesión existente o crea una nueva.
     */
    private static void cambiarSesion(String nuevoSessionId) {
        // Guardar sesión actual si existe
        if (sessionIdActual != null) {
            guardarSesion(sessionIdActual, historialActual);
            System.out.println("💾 Sesión guardada: " + sessionIdActual);
        }

        // Cambiar a nueva sesión
        sessionIdActual = nuevoSessionId;
        historialActual = cargarSesion(sessionIdActual);

        if (historialActual.isEmpty()) {
            System.out.println("📝 Nueva sesión creada: " + sessionIdActual);
            historialActual.add(new Mensaje("system",
                    "Eres un asistente amigable y servicial. "
                    + "Recuerda el contexto de la conversación."));
        } else {
            System.out.println("📂 Sesión cargada: " + sessionIdActual 
                    + " (" + (historialActual.size() - 1) + " mensajes)");
        }
    }

    /**
     * Lista todas las sesiones disponibles.
     */
    private static void listarSesiones() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📋 SESIONES DISPONIBLES");
        System.out.println("═".repeat(60));

        try (Stream<Path> archivos = Files.list(SESSIONS_DIR)) {
            List<Path> sesiones = archivos
                    .filter(p -> p.toString().endsWith(".json"))
                    .sorted()
                    .toList();

            if (sesiones.isEmpty()) {
                System.out.println("(No hay sesiones guardadas)");
            } else {
                for (Path sesion : sesiones) {
                    String nombre = sesion.getFileName().toString()
                            .replace(".json", "");
                    long size = Files.size(sesion);
                    String marca = nombre.equals(sessionIdActual) ? " ← ACTUAL" : "";

                    System.out.printf("  📁 %-20s (%,d bytes)%s%n", nombre, size, marca);
                }
                System.out.println("\nTotal: " + sesiones.size() + " sesión(es)");
            }

        } catch (IOException e) {
            System.err.println("❌ Error listando sesiones: " + e.getMessage());
        }

        System.out.println("═".repeat(60) + "\n");
    }

    /**
     * Muestra información de la sesión actual.
     */
    private static void mostrarInfo() {
        if (sessionIdActual == null) {
            System.out.println("⚠️ No hay sesión activa.");
            return;
        }

        System.out.println("\n" + "═".repeat(60));
        System.out.println("ℹ️  INFORMACIÓN DE LA SESIÓN");
        System.out.println("═".repeat(60));
        System.out.println("  ID:            " + sessionIdActual);
        System.out.println("  Mensajes:      " + historialActual.size());
        System.out.println("  Archivo:       " + obtenerArchivo(sessionIdActual));

        // Contar por rol
        long systemCount = historialActual.stream().filter(m -> m.rol().equals("system")).count();
        long userCount = historialActual.stream().filter(m -> m.rol().equals("user")).count();
        long assistantCount = historialActual.stream().filter(m -> m.rol().equals("assistant")).count();

        System.out.println("  - System:      " + systemCount);
        System.out.println("  - Usuario:     " + userCount);
        System.out.println("  - Asistente:   " + assistantCount);
        System.out.println("═".repeat(60) + "\n");
    }

    /**
     * Borra una sesión del disco.
     */
    private static void borrarSesion(String sessionId) {
        Path archivo = obtenerArchivo(sessionId);

        if (!Files.exists(archivo)) {
            System.out.println("⚠️ La sesión '" + sessionId + "' no existe.");
            return;
        }

        try {
            Files.delete(archivo);
            System.out.println("🗑️  Sesión eliminada: " + sessionId);

            // Si es la sesión actual, limpiarla
            if (sessionId.equals(sessionIdActual)) {
                sessionIdActual = null;
                historialActual = new ArrayList<>();
                System.out.println("   (Sesión actual cerrada)");
            }

        } catch (IOException e) {
            System.err.println("❌ Error eliminando sesión: " + e.getMessage());
        }
    }

    // ========== Métodos auxiliares (reutilizados de ChatbotConMemoriaPersistente) ==========

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

    private static void guardarSesion(String sessionId, List<Mensaje> historial) {
        Path archivo = obtenerArchivo(sessionId);
        try {
            String json = convertirAJson(historial);
            Files.writeString(archivo, json);
        } catch (IOException e) {
            System.err.println("❌ Error guardando sesión: " + e.getMessage());
        }
    }

    private static Path obtenerArchivo(String sessionId) {
        String cleanId = sessionId.replaceAll("[^a-zA-Z0-9_-]", "_");
        return SESSIONS_DIR.resolve(cleanId + ".json");
    }

    private static String convertirAJson(List<Mensaje> mensajes) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            json.append("  {")
                .append("\"rol\":\"").append(escapeJson(m.rol())).append("\",")
                .append("\"contenido\":\"").append(escapeJson(m.contenido())).append("\"")
                .append("}");
            if (i < mensajes.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }

    private static List<Mensaje> parsearJson(String json) {
        List<Mensaje> mensajes = new ArrayList<>();
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) return mensajes;
        json = json.substring(1, json.length() - 1).trim();

        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    String objeto = json.substring(start + 1, i);
                    Mensaje mensaje = parsearObjeto(objeto);
                    if (mensaje != null) mensajes.add(mensaje);
                    start = -1;
                }
            }
        }
        return mensajes;
    }

    private static Mensaje parsearObjeto(String objeto) {
        String rol = extraerValor(objeto, "rol");
        String contenido = extraerValor(objeto, "contenido");
        return (rol != null && contenido != null) ? new Mensaje(rol, contenido) : null;
    }

    private static String extraerValor(String objeto, String clave) {
        String patron = "\"" + clave + "\":\"";
        int inicio = objeto.indexOf(patron);
        if (inicio == -1) return null;
        inicio += patron.length();
        int fin = inicio;
        while (fin < objeto.length()) {
            char c = objeto.charAt(fin);
            if (c == '\\') fin += 2;
            else if (c == '"') break;
            else fin++;
        }
        return unescapeJson(objeto.substring(inicio, fin));
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

    private static void mostrarHistorial(List<Mensaje> historial) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📋 HISTORIAL DE LA SESIÓN: " + sessionIdActual);
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
                if (contenido.length() > 150) contenido = contenido.substring(0, 147) + "...";
                System.out.println("    " + contenido);
            }
        }
        System.out.println("═".repeat(60) + "\n");
    }
}
