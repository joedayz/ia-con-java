package com.joedayz.ia.langchain4j.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolCallingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolCallingApplication.class, args);

        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Tool Calling con LangChain4j + Ollama             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("✅ Aplicación iniciada exitosamente");
        System.out.println();
        System.out.println("📍 Endpoints disponibles:");
        System.out.println("   POST http://localhost:8082/api/tool-calling/chat");
        System.out.println("        → Chat con todas las herramientas disponibles");
        System.out.println();
        System.out.println("🛠️  Herramientas registradas (@Tool):");
        System.out.println("   • Calculadora → sumar, restar, multiplicar, dividir, raíz, potencia (Lab 14)");
        System.out.println("   • Fecha       → fechaActual, fechaHoraActual (Lab 14)");
        System.out.println("   • País API    → consultarPais via restcountries.com (Reto)");
        System.out.println();
        System.out.println("📚 Swagger UI:");
        System.out.println("   http://localhost:8082/swagger-ui.html");
        System.out.println();
        System.out.println("🦙 Ollama:");
        System.out.println("   Base URL: http://localhost:11434");
        System.out.println("   Chat model: llama3.2");
        System.out.println();
    }
}
