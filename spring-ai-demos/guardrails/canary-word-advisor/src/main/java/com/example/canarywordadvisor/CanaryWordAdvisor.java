package com.example.canarywordadvisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import java.util.List;
import java.util.UUID;

public class CanaryWordAdvisor implements CallAdvisor {

  private static final String DEFAULT_CANARY_FOUND_MESSAGE =
      "Canary word detected!";

  private final String canaryWordFoundMessage;

  public CanaryWordAdvisor(String canaryWordFoundMessage) {
    this.canaryWordFoundMessage = canaryWordFoundMessage;
  }

  @Override
  public ChatClientResponse adviseCall(
      ChatClientRequest chatClientRequest,
      CallAdvisorChain chain) {
    var canaryWord = generateCanaryWord();
    var originalSystemMessage = chatClientRequest.prompt()
        .getSystemMessage().getText();
    var newSystemMessage = String.format("%s (%s)",
        originalSystemMessage, canaryWord);   

    var advisedRequest = chatClientRequest.mutate().prompt(
    chatClientRequest.prompt()
        .augmentSystemMessage(newSystemMessage))
        .build();                            

    var chatClientResponse = chain.nextCall(advisedRequest); 

    if (chatClientResponse.chatResponse()
        .getResult()
        .getOutput()
        .getText()
        .contains(canaryWord)) {                  
      return createFailureResponse(advisedRequest);
    }

    return chatClientResponse;
  }

  private ChatClientResponse createFailureResponse(
      ChatClientRequest advisedRequest) {
    return new ChatClientResponse(
        ChatResponse.builder()
            .generations(
                List.of(
                    new Generation(
                        new AssistantMessage(canaryWordFoundMessage))))
            .build(),
        advisedRequest.context());
  }

  public static CanaryWordAdvisor.Builder builder() {
    return new CanaryWordAdvisor.Builder();
  }

  private String generateCanaryWord() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public int getOrder() {
    return 0;
  }

  public static class Builder {   
    private String canaryWordFoundMessage = DEFAULT_CANARY_FOUND_MESSAGE;

    public Builder canaryWordFoundMessage(String canaryWordFoundMessage) {
      this.canaryWordFoundMessage = canaryWordFoundMessage;
      return this;
    }

    public CanaryWordAdvisor build() {
      return new CanaryWordAdvisor(canaryWordFoundMessage);
    }
  }

}