package com.example.boardgamebuddy;

import com.example.boardgamebuddy.gamedata.GameTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AiConfig.class);

  
  @Bean
  ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                        VectorStore vectorStore) {
    return chatClientBuilder
        .defaultAdvisors(
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().build()).build(),
            MessageChatMemoryAdvisor.builder(
                MessageWindowChatMemory.builder().build()).build())
        .defaultToolNames("gameTools")
        .build();
  }
  

}
