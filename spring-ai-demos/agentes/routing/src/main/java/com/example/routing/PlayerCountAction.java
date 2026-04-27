package com.example.routing;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class PlayerCountAction implements Action {

  private static final Logger LOGGER =
      Logger.getLogger(PlayerCountAction.class.getName());

  private final ChatClient chatClient;

  public PlayerCountAction(
      ChatClient.Builder chatClientBuilder,
      @Value("classpath:/promptTemplates/playerCount.st")
      Resource systemMessageTemplate) {
    this.chatClient = chatClientBuilder
        .defaultSystem(systemMessageTemplate)
        .build();
  }

  @Override
  public String act(String rules) {
    LOGGER.info("Getting player count from rules.");
    return chatClient.prompt()
        .user(userSpec -> userSpec
            .text("Analyze the following rules:\n\nRULES:\n\n{rules}")
            .param("rules", rules))
        .call()
        .content();
  }
}
