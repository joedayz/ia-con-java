package com.joedayz.ia.fase3;

import com.joedayz.ia.common.service.ServicioIA;
import com.joedayz.ia.common.service.ServicioIA.Mensaje;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo comparativa: Chatbot SIN memoria vs CON memoria.
 * 
 * Muestra lado a lado cómo responde un LLM con y sin contexto previo.
 * Útil para demostrar la importancia de la memoria conversacional en clase.
 */
public class ComparacionMemoria {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   COMPARACIÓN: Chatbot CON vs SIN Memoria                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        ServicioIA servicio = new ServicioIA();
        String systemPrompt = "Eres un asistente amigable. Responde de forma concisa.";

        // Secuencia de preguntas para probar memoria
        String[] preguntas = {
            "Hola, me llamo Ana y trabajo en desarrollo de software",
            "¿Cuál es mi nombre?",
            "¿A qué me dedico?",
            "¿Puedes recordar mi nombre y profesión?"
        };

        for (int i = 0; i < preguntas.length; i++) {
            String pregunta = preguntas[i];

            System.out.println("═".repeat(70));
            System.out.println("PREGUNTA " + (i + 1) + ": \"" + pregunta + "\"");
            System.out.println("═".repeat(70));

            // 1. SIN MEMORIA (cada llamada es independiente)
            System.out.println("\n❌ SIN MEMORIA:");
            System.out.println("─".repeat(70));
            try {
                String respuestaSinMemoria = servicio.chat(systemPrompt, pregunta);
                System.out.println("🤖 " + respuestaSinMemoria);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            // 2. CON MEMORIA (acumula contexto)
            System.out.println("\n✅ CON MEMORIA:");
            System.out.println("─".repeat(70));
            try {
                String respuestaConMemoria = chatConMemoriaAcumulada(servicio, systemPrompt, pregunta, i);
                System.out.println("🤖 " + respuestaConMemoria);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            System.out.println();
        }

        System.out.println("═".repeat(70));
        System.out.println("CONCLUSIÓN:");
        System.out.println("═".repeat(70));
        System.out.println("✅ CON MEMORIA: El LLM recuerda el contexto y responde coherentemente");
        System.out.println("❌ SIN MEMORIA: Cada pregunta es independiente, sin contexto previo");
        System.out.println();
        System.out.println("💡 La memoria es esencial para conversaciones naturales y contextuales.");
        System.out.println("═".repeat(70));
    }

    // Historial compartido para simular memoria acumulada
    private static final List<Mensaje> historialGlobal = new ArrayList<>();

    static {
        // Inicializar con system prompt
        historialGlobal.add(new Mensaje("system", 
            "Eres un asistente amigable. Responde de forma concisa."));
    }

    /**
     * Simula chat con memoria acumulada a través de las preguntas.
     */
    private static String chatConMemoriaAcumulada(ServicioIA servicio, String systemPrompt, 
                                                  String pregunta, int indice) {
        // Agregar pregunta al historial
        historialGlobal.add(Mensaje.usuario(pregunta));

        // Enviar con historial completo
        List<Mensaje> paraEnviar = new ArrayList<>(historialGlobal);
        String respuesta = enviarConHistorial(servicio, paraEnviar);

        // Agregar respuesta al historial
        historialGlobal.add(Mensaje.asistente(respuesta));

        return respuesta;
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
}
