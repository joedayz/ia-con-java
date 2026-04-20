package com.joedayz.ia.langchain4j.tools.config;

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
                        .title("Tool Calling - LangChain4j + Ollama")
                        .description("API de demostración de Tool Calling con LangChain4j y Ollama. "
                                + "Lab 14: Calculadora y fechaActual() con @Tool. "
                                + "Reto: consultarPais con API REST real (restcountries.com).")
                        .version("1.0.0"));
    }
}
