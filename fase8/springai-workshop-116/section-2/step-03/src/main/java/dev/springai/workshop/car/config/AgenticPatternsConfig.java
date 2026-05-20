package dev.springai.workshop.car.config;

import dev.springai.workshop.agentic.ChainWorkflow;
import dev.springai.workshop.agentic.ParallelizationWorkflow;
import dev.springai.workshop.agentic.RoutingWorkflow;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgenticPatternsConfig {

    @Bean
    ChainWorkflow chainWorkflow(ChatModel chatModel) {
        return new ChainWorkflow(ChatClient.builder(chatModel).build());
    }

    @Bean
    ParallelizationWorkflow parallelizationWorkflow(ChatModel chatModel) {
        return new ParallelizationWorkflow(ChatClient.builder(chatModel).build());
    }

    @Bean
    RoutingWorkflow routingWorkflow(ChatModel chatModel) {
        return new RoutingWorkflow(ChatClient.builder(chatModel).build());
    }
}
