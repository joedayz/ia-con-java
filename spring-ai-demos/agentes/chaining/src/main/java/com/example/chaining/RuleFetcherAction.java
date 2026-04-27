package com.example.chaining;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class RuleFetcherAction implements Action {

  private static final Logger LOGGER =
      Logger.getLogger(RuleFetcherAction.class.getName());

  private final ChatClient chatClient;
  private final String rulesFilePath;
  private final ResourceLoader resourceLoader;

  public RuleFetcherAction(
      ChatClient.Builder chatClientBuilder,
      @Value("${boardgame.rules.path:}")
      String rulesFilePath,
      ResourceLoader resourceLoader,
      @Value("classpath:/promptTemplates/rulesFetcher.st")
      Resource systemMessageTemplate) {
    this.chatClient = chatClientBuilder
        .defaultSystem(systemMessageTemplate)
        .build();
    this.rulesFilePath = rulesFilePath;
    this.resourceLoader = resourceLoader;
  }

  public String act(String input) {
    LOGGER.info("Fetching rules for: " + input);
    var rulesFile = chatClient.prompt()
        .user(user -> user.text(input))
        .call()
        .entity(RulesFile.class);

    if (rulesFile.successful()) {
      String rulesContent = loadRules(rulesFile.filename());
      if (rulesContent != null) {
        return rulesContent;
      }
    }

    throw new ActionFailedException("Unable to fetch rules for the specified game.");
  }

  private String loadRules(String filename) {
    if (rulesFilePath == null || rulesFilePath.isBlank()) {
      throw new ActionFailedException("""
          Missing required property 'boardgame.rules.path'.

          Set it to a local folder containing the rules files, for example:
            boardgame.rules.path=file:/absolute/path/to/BoardGameRules
          """);
    }

    var base = rulesFilePath.endsWith("/") ? rulesFilePath.substring(0, rulesFilePath.length() - 1) : rulesFilePath;
    var rulesResource = resourceLoader.getResource(base + "/" + filename);

    return new TikaDocumentReader(rulesResource)
        .get()
        .getFirst()
        .getText();
  }

  private record RulesFile (boolean successful, String filename) {}

}
