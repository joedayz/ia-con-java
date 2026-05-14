package com.joedayz.ia.langchain4j.tools.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service interface para LangChain4j.
 *
 * LangChain4j genera automáticamente la implementación de esta interfaz
 * usando AiServices.builder(). Cada llamada a chat() envía el mensaje
 * al LLM junto con la descripción de las herramientas disponibles.
 */
public interface Assistant {

    @SystemMessage("""
            Eres un asistente útil que responde siempre en español.
            Tienes acceso a herramientas para:
            1. Operaciones matemáticas: sumar, restar, multiplicar, dividir, raíz cuadrada, potencia
            2. Consultar la fecha y hora actual
            3. Consultar información real de países mediante una API REST externa

            Usa las herramientas cuando sea necesario para responder con precisión.
            Para cálculos matemáticos, SIEMPRE usa las herramientas de calculadora en lugar de calcular tú mismo.
            """)
    String chat(@UserMessage String message);
}
