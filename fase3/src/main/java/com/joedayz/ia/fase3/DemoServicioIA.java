package com.joedayz.ia.fase3;

import com.joedayz.ia.common.service.ServicioIA;

/**
 * Demo de la Fase 3: uso del servicio de IA compartido (módulo common).
 */
public class DemoServicioIA {

    public static void main(String[] args) {
        ServicioIA servicio = new ServicioIA();

        String systemPrompt = "Eres un experto en Java. Responde en una sola frase.";
        String respuesta = servicio.chat(systemPrompt, "¿Qué es un record en Java 17?");
        System.out.println("Respuesta: " + respuesta);
    }
}
