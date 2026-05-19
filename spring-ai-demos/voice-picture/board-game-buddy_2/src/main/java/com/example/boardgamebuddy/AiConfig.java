package com.example.boardgamebuddy;

import com.example.boardgamebuddy.gamedata.Game;
import com.example.boardgamebuddy.gamedata.GameComplexity;
import com.example.boardgamebuddy.gamedata.GameComplexityRequest;
import com.example.boardgamebuddy.gamedata.GameComplexityResponse;
import com.example.boardgamebuddy.gamedata.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.Optional;
import java.util.function.Function;

@Configuration
public class AiConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AiConfig.class);

  @Bean
  ChatClient chatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
    return chatClientBuilder
        .defaultAdvisors(
            QuestionAnswerAdvisor.builder(vectorStore).build(),
            VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
        .defaultToolNames("gameComplexityFunction")
        .build();
  }

  @Description("Returns a game's complexity/difficulty " +
      "given the game's title/name.")
  Function<GameComplexityRequest, GameComplexityResponse>
      gameComplexityFunction(GameRepository gameRepository) {

    return gameDataRequest -> {
      var gameSlug = gameDataRequest.title()
          .toLowerCase()
          .replace(" ", "_"); 

      LOGGER.info("Getting complexity for {} ({})",
          gameDataRequest.title(), gameSlug);

      var gameOpt = gameRepository.findBySlug(gameSlug); 

      var game = gameOpt.orElseGet(() -> {
        LOGGER.warn("Game not found: {}", gameSlug);
        return new Game(
            null,
            gameSlug,
            gameDataRequest.title(),
            GameComplexity.UNKNOWN.getValue());
      }); 

      return new GameComplexityResponse(
          game.title(), game.complexityEnum());   
    };

  }

}
