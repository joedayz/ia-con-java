package com.example.summarization;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@SpringBootApplication
public class SummarizationApplication {

  public static void main(String[] args) {
    SpringApplication.run(SummarizationApplication.class, args);
  }

  @Value("classpath:/systemPrompt.st")
  Resource systemPrompt;

  @Value("${rules.resource:}")
  String rulesResourceLocation;

  @Bean
  ApplicationRunner go(ChatClient.Builder clientBuilder, ResourceLoader resourceLoader) {
    return args -> {
      if (rulesResourceLocation == null || rulesResourceLocation.isBlank()) {
        System.out.println("""
            No rules resource configured.

            Provide a PDF/TXT path using:
              -Drules.resource=file:/absolute/path/to/rules.pdf

            Or in application.properties:
              rules.resource=file:/absolute/path/to/rules.pdf
            """);
        return;
      }

      var chatClient = clientBuilder.build();
      var rulesResource = resourceLoader.getResource(rulesResourceLocation);

      var rulesText = new TikaDocumentReader(rulesResource)
          .get().get(0).getText();

      var summary = chatClient.prompt()
          .system(systemSpec -> systemSpec
              .text(systemPrompt)
              .param("gameRules", rulesText))
          .user("Summarize the rules.")
          .call()
          .content();

      System.out.println("Summary: \n--------\n" + summary);
    };
  }

}
