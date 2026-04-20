package com.joedayz.ia.langchain4j.tools.tool;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Lab 14: Herramientas de calculadora con @Tool de LangChain4j.
 *
 * Cada método anotado con @Tool es detectado automáticamente por
 * LangChain4j y presentado al LLM como una función disponible.
 * El LLM decide cuándo invocar cada operación matemática.
 */
@Component
public class CalculadoraTools {

    private static final Logger log = LoggerFactory.getLogger(CalculadoraTools.class);

    @Tool("Suma dos números y retorna el resultado")
    public double sumar(double a, double b) {
        log.info("🧮 sumar({}, {}) = {}", a, b, a + b);
        return a + b;
    }

    @Tool("Resta dos números (a - b) y retorna el resultado")
    public double restar(double a, double b) {
        log.info("🧮 restar({}, {}) = {}", a, b, a - b);
        return a - b;
    }

    @Tool("Multiplica dos números y retorna el resultado")
    public double multiplicar(double a, double b) {
        log.info("🧮 multiplicar({}, {}) = {}", a, b, a * b);
        return a * b;
    }

    @Tool("Divide dos números (a / b) y retorna el resultado. Retorna mensaje de error si b es cero.")
    public String dividir(double a, double b) {
        if (b == 0) {
            log.warn("🧮 dividir({}, {}) → Error: división por cero", a, b);
            return "Error: no se puede dividir por cero";
        }
        double result = a / b;
        log.info("🧮 dividir({}, {}) = {}", a, b, result);
        return String.valueOf(result);
    }

    @Tool("Calcula la raíz cuadrada de un número")
    public double raizCuadrada(double numero) {
        double result = Math.sqrt(numero);
        log.info("🧮 raizCuadrada({}) = {}", numero, result);
        return result;
    }

    @Tool("Calcula la potencia de un número (base elevado a exponente)")
    public double potencia(double base, double exponente) {
        double result = Math.pow(base, exponente);
        log.info("🧮 potencia({}, {}) = {}", base, exponente, result);
        return result;
    }
}
