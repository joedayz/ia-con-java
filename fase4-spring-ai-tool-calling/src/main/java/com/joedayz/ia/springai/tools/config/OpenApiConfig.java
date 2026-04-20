package com.joedayz.ia.springai.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tool Calling - Spring AI + Ollama")
                        .description("API de demostración de Tool Calling (Function Calling) con Spring AI y Ollama. "
                                + "Lab 13: herramienta obtenerClima como @Bean. "
                                + "Reto: herramienta consultarPais con API REST real (restcountries.com).")
                        .version("1.0.0"));
    }
}
