package com.example.chaining;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentWorkflowConfig {

  @Bean
  Chain chain(
      RuleFetcherAction ruleFetcher,
      MechanicsDeterminerAction mechanicsDeterminer) {
    return new Chain(List.of(ruleFetcher, mechanicsDeterminer));
  }

}
