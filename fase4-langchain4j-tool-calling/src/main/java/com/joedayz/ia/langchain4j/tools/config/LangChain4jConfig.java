package com.joedayz.ia.langchain4j.tools.config;

import com.joedayz.ia.langchain4j.tools.service.Assistant;
import com.joedayz.ia.langchain4j.tools.tool.CalculadoraTools;
import com.joedayz.ia.langchain4j.tools.tool.FechaTools;
import com.joedayz.ia.langchain4j.tools.tool.PaisApiTools;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de LangChain4j AI Services con Tool Calling.
 *
 * AiServices conecta la interfaz Assistant con el modelo de chat
 * y las herramientas (@Tool) disponibles. LangChain4j genera
 * automáticamente la implementación del proxy.
 *
 * Flujo:
 * 1. El usuario envía un mensaje → Assistant.chat(message)
 * 2. LangChain4j envía al LLM la lista de tools disponibles
 * 3. El LLM decide si necesita una herramienta
 * 4. LangChain4j ejecuta el método @Tool correspondiente
 * 5. El resultado se devuelve al LLM para generar la respuesta final
 */
@Configuration
public class LangChain4jConfig {

    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel,
                               CalculadoraTools calculadoraTools,
                               FechaTools fechaTools,
                               PaisApiTools paisApiTools) {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(calculadoraTools, fechaTools, paisApiTools)
                .build();
    }
}
