package com.joedayz.ia.fase3;

import com.joedayz.ia.fase3.ollama.ServicioIAOllama;
import com.joedayz.ia.fase3.ollama.ServicioIAOllama.Mensaje;

/**
 * Demo del servicio de IA con Ollama.
 *
 * Ejecutar:
 *   mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.DemoServicioIA"
 */
public class DemoServicioIA {

    public static void main(String[] args) {
        ServicioIAOllama servicio = new ServicioIAOllama();

        System.out.println("🦙 Usando modelo: " + servicio.getModel());
        System.out.println("🌐 URL: " + servicio.getBaseUrl());
        System.out.println();

        String systemPrompt = "Eres un experto en Java. Responde en una sola frase.";
        String respuesta = servicio.chat(systemPrompt, "¿Qué es un record en Java 17?");
        System.out.println("Respuesta: " + respuesta);
    }
}

