package com.joedayz.ia.fase5.mcp.provider;

import com.joedayz.ia.fase5.mcp.provider.capabilities.CursoPromptProvider;
import com.joedayz.ia.fase5.mcp.provider.capabilities.CursoResourceProvider;
import com.joedayz.ia.fase5.mcp.provider.capabilities.CursoToolProvider;
import com.logaritex.mcp.spring.SpringAiMcpAnnotationProvider;
import io.modelcontextprotocol.server.McpServerFeatures.SyncPromptSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import java.util.List;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Fase5McpProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(Fase5McpProviderApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider cursoTools(CursoToolProvider cursoToolProvider) {
        return MethodToolCallbackProvider.builder().toolObjects(cursoToolProvider).build();
    }

    @Bean
    public List<SyncResourceSpecification> resourceSpecs(CursoResourceProvider resourceProvider) {
        return SpringAiMcpAnnotationProvider.createSyncResourceSpecifications(List.of(resourceProvider));
    }

    @Bean
    public List<SyncPromptSpecification> promptSpecs(CursoPromptProvider promptProvider) {
        return SpringAiMcpAnnotationProvider.createSyncPromptSpecifications(List.of(promptProvider));
    }
}

