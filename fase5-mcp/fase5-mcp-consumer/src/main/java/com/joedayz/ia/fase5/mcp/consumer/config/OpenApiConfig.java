package com.joedayz.ia.fase5.mcp.consumer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Fase 5 MCP Consumer")
                .description("Consumidor MCP con ChatClient + ToolCallbackProvider conectado por SSE al provider")
                .version("1.0.0"));
    }
}

