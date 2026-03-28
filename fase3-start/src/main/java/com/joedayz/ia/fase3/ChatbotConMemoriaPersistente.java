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
 * LAB 8: Chatbot con memoria persistente (guardada en archivo JSON).
 * 
 * OBJETIVO:
 * Implementar persistencia de conversaciones para que sobrevivan a reinicios.
 * Cada usuario tiene su propio archivo JSON con su historial.
 * 
 * TAREA:
 * Completa los métodos marcados con TODO para:
 * 1. Guardar conversaciones en archivos JSON
 * 2. Cargar conversaciones desde archivos JSON
 * 3. Gestionar múltiples sesiones de usuario
 * 
 * CONCEPTOS CLAVE:
 * - Persistent Memory: sobrevive a reinicios
 * - JSON serialization: convertir objetos a/desde JSON
 * - File I/O: leer y escribir archivos
 * 
 * ESTRUCTURA:
 * fase3-start/sessions/
 *   usuario-123.json
 *   maria.json
 */
public class ChatbotConMemoriaPersistente {

    // Directorio donde se guardan las sesiones
    private static final Path SESSIONS_DIR = Paths.get("fase3-start/sessions");

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Lab 8: Chatbot con Memoria Persistente          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        // TODO: Solicitar ID de sesión al usuario
        System.out.print("Ingresa un ID de sesión (ej. usuario123): ");
        String sessionId = ""; // Leer del scanner

        System.out.println("─".repeat(60));

        // TODO: Crear directorio de sesiones si no existe
        // Pista: Files.createDirectories(SESSIONS_DIR);

        // TODO: Cargar historial existente o crear nuevo
        // Pista: usa el método cargarSesion(sessionId) que está abajo
        List<Mensaje> historial = new ArrayList<>();

        // TODO: Si el historial está vacío, agregar system prompt inicial
        // Si no está vacío, informar cuántos mensajes se cargaron

        System.out.println();
        System.out.println("Comandos especiales:");
        System.out.println("  /historial - Ver mensajes guardados");
        System.out.println("  salir      - Guardar y terminar");
        System.out.println("─".repeat(60));

        ServicioIA servicio = new ServicioIA();

        while (true) {
            System.out.print("\n💬 Tú: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir") || entrada.equalsIgnoreCase("exit")) {
                // TODO: Guardar la sesión antes de salir
                // Pista: guardarSesion(sessionId, historial);
                System.out.println("\n💾 Conversación guardada.");
                System.out.println("👋 ¡Hasta luego!");
                break;
            }

            if (entrada.isEmpty()) {
                continue;
            }

            // TODO: Implementar comando /historial

            try {
                // TODO: 1. Agregar mensaje del usuario al historial
                
                // TODO: 2. Enviar al LLM (reutiliza código del Lab 7)

                // TODO: 3. Agregar respuesta del asistente al historial

                // TODO: 4. IMPORTANTE - Guardar automáticamente después de cada mensaje
                // Pista: guardarSesion(sessionId, historial);

                // TODO: 5. Mostrar respuesta

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * TODO: Implementa este método para cargar una sesión desde archivo JSON.
     * 
     * PASOS:
     * 1. Obtener la ruta del archivo con obtenerArchivo(sessionId)
     * 2. Si no existe, retornar lista vacía
     * 3. Leer el contenido del archivo: Files.readString(archivo)
     * 4. Parsear el JSON usando parsearJson(contenido)
     * 5. Retornar la lista de mensajes
     */
    private static List<Mensaje> cargarSesion(String sessionId) {
        // TODO: Implementar aquí
        return new ArrayList<>();
    }

    /**
     * TODO: Implementa este método para guardar una sesión en archivo JSON.
     * 
     * PASOS:
     * 1. Obtener la ruta del archivo con obtenerArchivo(sessionId)
     * 2. Convertir el historial a JSON usando convertirAJson(historial)
     * 3. Escribir el JSON al archivo: Files.writeString(archivo, json)
     */
    private static void guardarSesion(String sessionId, List<Mensaje> historial) {
        // TODO: Implementar aquí
    }

    /**
     * Obtiene la ruta del archivo de sesión.
     * Ya está implementado - sanitiza el ID para seguridad.
     */
    private static Path obtenerArchivo(String sessionId) {
        String cleanId = sessionId.replaceAll("[^a-zA-Z0-9_-]", "_");
        return SESSIONS_DIR.resolve(cleanId + ".json");
    }

    /**
     * TODO: Implementa este método para convertir mensajes a JSON.
     * 
     * FORMATO ESPERADO:
     * [
     *   {"rol":"system","contenido":"Eres un asistente..."},
     *   {"rol":"user","contenido":"Hola"},
     *   {"rol":"assistant","contenido":"Hola, ¿en qué puedo ayudarte?"}
     * ]
     * 
     * PISTAS:
     * - Usa StringBuilder
     * - Empieza con "[\n"
     * - Para cada mensaje, agrega: {"rol":"...","contenido":"..."}
     * - No olvides escapar caracteres especiales con escapeJson()
     * - Termina con "]"
     */
    private static String convertirAJson(List<Mensaje> mensajes) {
        // TODO: Implementar aquí
        return "[]";
    }

    /**
     * TODO BONUS: Implementa este método para parsear JSON a lista de mensajes.
     * 
     * PISTAS:
     * - Busca objetos entre { y }
     * - Para cada objeto, extrae "rol" y "contenido"
     * - Usa los métodos auxiliares parsearObjeto() y extraerValor()
     * 
     * NOTA: Esta es la parte más compleja. Si tienes dudas, consulta
     * la solución en fase3/ChatbotConMemoriaPersistente.java
     */
    private static List<Mensaje> parsearJson(String json) {
        // TODO: Implementar aquí (BONUS - complejo)
        return new ArrayList<>();
    }

    /**
     * Parsea un objeto JSON individual.
     * TODO: Implementar si haces el BONUS.
     */
    private static Mensaje parsearObjeto(String objeto) {
        // TODO: Implementar aquí (BONUS)
        return null;
    }

    /**
     * Extrae el valor de una clave en JSON.
     * TODO: Implementar si haces el BONUS.
     */
    private static String extraerValor(String objeto, String clave) {
        // TODO: Implementar aquí (BONUS)
        return null;
    }

    /**
     * Escapa caracteres especiales para JSON.
     * Ya está implementado.
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
     * Ya está implementado.
     */
    private static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
