package com.example.boardgamebuddy;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {

  private final BoardGameService boardGameService;

  public AskController(BoardGameService boardGameService) {
    this.boardGameService = boardGameService;
  }

  @PostMapping(path = "/ask", produces = "application/json")
  public Answer ask(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestBody Question question) {
    return boardGameService.askQuestion(question, conversationId);
  }

}
