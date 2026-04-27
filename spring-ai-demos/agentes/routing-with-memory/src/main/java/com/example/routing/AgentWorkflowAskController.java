package com.example.routing;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentWorkflowAskController {

  private final Router router;

  public AgentWorkflowAskController(Router router) {
    this.router = router;
  }

  @PostMapping("/ask")
  public Answer ask(@RequestBody Question question) {
    var response = router.act(question.question());
    return new Answer(response);
  }

}
