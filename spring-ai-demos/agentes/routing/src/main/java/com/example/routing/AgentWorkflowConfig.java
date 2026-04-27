package com.example.routing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentWorkflowConfig {

  @Bean
  public Chain mechanics(
      RuleFetcherAction ruleFetcher,
      MechanicsDeterminerAction mechanicsDeterminer) {
    return new Chain(List.of(ruleFetcher, mechanicsDeterminer));
  }

  @Bean
  public Chain playerCount(
      RuleFetcherAction ruleFetcher,
      PlayerCountAction playerCountTask) {
    return new Chain(List.of(ruleFetcher, playerCountTask));
  }

}
