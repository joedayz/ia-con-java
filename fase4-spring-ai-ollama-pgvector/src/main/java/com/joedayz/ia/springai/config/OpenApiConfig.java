package com.joedayz.ia.springai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
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

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fase 4 - Spring AI + Ollama + PgVector API")
                        .version("1.0.0")
                        .description("""
                                API REST para chatbot con RAG usando Spring AI, Ollama y PgVector.
                                
                                **Características:**
                                - 💬 Chat con memoria conversacional persistente en PostgreSQL
                                - 🔍 Búsqueda semántica con embeddings almacenados en PgVector
                                - 📄 Carga y análisis de PDFs con Tika
                                - 🧠 RAG (Retrieval Augmented Generation) con vector store persistente
                                - 🦙 Integración local con Ollama para chat y embeddings
                                - 🐘 PostgreSQL + PgVector como vector store (HNSW index)
                                
                                **Proveedor:** OLLAMA + PGVECTOR
                                
                                **Diferencia con Fase 3:**
                                - Vector store persistente (PgVector) en vez de SimpleVectorStore en memoria
                                - Los documentos indexados sobreviven reinicios de la aplicación
                                - Memoria de chat persistente en PostgreSQL en vez de H2
                                - Índice HNSW para búsquedas vectoriales eficientes
                                """)
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
                                .description("Servidor local de desarrollo")));
    }
}
