package com.example.parallel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentWorkflowConfig {

  @Bean
  ParallelizerAction parallelAction(
      PlayerCountAction playerCount,
      MechanicsDeterminerAction mechanicsDeterminer) {
    return new ParallelizerAction(
        List.of(playerCount, mechanicsDeterminer));
  }

  @Bean
  public Chain summarizerChain(
      RuleFetcherAction ruleFetcher,
      ParallelizerAction parallelizerAction,
      SummarizerAction summarizer) {
    return new Chain(
        List.of(ruleFetcher, parallelizerAction, summarizer));
  }

}
