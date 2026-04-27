package com.example.parallel;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class MechanicsDeterminerAction implements Action {

  private static final Logger LOGGER =
      Logger.getLogger(MechanicsDeterminerAction.class.getName());

  private final ChatClient chatClient;

  public MechanicsDeterminerAction(
      ChatClient.Builder chatClientBuilder,
      @Value("classpath:/promptTemplates/mechanicsDeterminer.st")
      Resource systemMessageTemplate) {
    this.chatClient = chatClientBuilder
        .defaultSystem(systemMessageTemplate)
        .build();
  }

  @Override
  public String act(String rules) {
    LOGGER.info("Determining mechanics from rules.");
    return chatClient.prompt()
        .user(userSpec -> userSpec
            .text("Analyze the following rules:\n\nRULES:\n\n{rules}")
            .param("rules", rules))
        .call()
        .content();
  }
}
