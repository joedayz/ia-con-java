package com.example.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Router implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

  private final Map<String, Chain> chains;
  private final Resource systemMessageTemplate;
  private final ChatClient chatClient;

  public Router(ChatClient.Builder chatClientBuilder,
                Map<String, Chain> chains,
                @Value("classpath:/promptTemplates/router.st")
                Resource systemMessageTemplate) {
    this.chains = chains;
    this.systemMessageTemplate = systemMessageTemplate;
    this.chatClient = chatClientBuilder.build();
  }

  public String act(String input) {
    var handler = chatClient.prompt()
        .system(systemMessageTemplate)
        .user(userSpec -> userSpec
            .text("Choose a handler for the following input: {userInput}")
            .param("userInput", input))
        .call()
        .entity(Handler.class);
    LOGGER.info("Routing to {} for input: {}", handler.handlerName(), input);
    return chains.get(handler.handlerName()).act(input);
  }

  private record Handler(String handlerName) {}

}
