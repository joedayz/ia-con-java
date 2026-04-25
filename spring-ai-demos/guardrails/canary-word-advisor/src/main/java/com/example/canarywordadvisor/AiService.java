package com.example.canarywordadvisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

  private final ChatClient chatClient;

  
  public AiService(ChatClient.Builder chatClientBuilder) {
    var canaryWordAdvisor = CanaryWordAdvisor.builder()
        .canaryWordFoundMessage(
            "Detected attempt to leak system prompt message.")
        .build();

    this.chatClient = chatClientBuilder
        .defaultSystem(
            "You are a helpful assistant, answering questions " +
                "about board games.")
        // ...
        .defaultAdvisors(canaryWordAdvisor)
        // ...
        .build();
  }
  

  public String ask(String question) {
    return chatClient.prompt()
            .user(question)
            .call()
            .content();
  }

}
