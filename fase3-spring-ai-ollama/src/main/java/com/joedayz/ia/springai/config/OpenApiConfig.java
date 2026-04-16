package com.joedayz.ia.springai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI / Swagger UI para documentación interactiva de la API.
 * 
 * Accede a la documentación en: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.chat.provider:ollama}")
    private String currentProvider;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fase 3 - Spring AI Chatbot API")
                        .version("1.0.0")
                        .description("""
                                API REST para chatbot con memoria conversacional y búsqueda semántica (RAG).
                                
                                **Características:**
                                - 💬 Chat con memoria conversacional (RAM o persistente)
                                - 🔍 Búsqueda semántica con embeddings
                                - 📄 Carga y análisis de PDFs con Tika
                                - 🧠 RAG (Retrieval Augmented Generation)
                                - 🦙 Integración local con Ollama para chat y embeddings
                                
                                **Proveedor actual:** %s
                                
                                **Labs incluidos:**
                                - Lab 7: Memoria en RAM
                                - Lab 8: Multi-sesión y limpieza de contexto
                                - Lab 9-10: Embeddings + búsqueda semántica
                                - Lab 11-12: RAG manual y con advisor
                                """.formatted(currentProvider.toUpperCase()))
                        .contact(new Contact()
                                .name("JoeDayz")
                                .url("https://joedayz.com")
                                .email("contacto@joedayz.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor local de desarrollo"),
                        new Server()
                                .url("https://api-produccion.ejemplo.com")
                                .description("Servidor de producción (ejemplo)")
                ));
    }
}
