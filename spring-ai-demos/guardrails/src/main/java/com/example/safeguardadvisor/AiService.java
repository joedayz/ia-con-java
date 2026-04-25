package com.example.safeguardadvisor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiService {

  private final ChatClient chatClient;

  public AiService(ChatClient.Builder chatClientBuilder) {
    var safeGuardAdvisor = SafeGuardAdvisor.builder()
        .sensitiveWords(List.of("Uno", "uno", "UNO"))
        .failureResponse("We don't talk about UNO. No no no... " +
            "We don't talk about UNO. But...")
        .build();

    
    this.chatClient = chatClientBuilder
    // ...
        .defaultAdvisors(safeGuardAdvisor)
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
