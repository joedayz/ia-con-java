package com.example.safeguardadvisor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AiServiceTest {

  @Autowired
  AiService aiService;

  @Test
  public void shouldFilterSensitiveWords() {
    var answer = aiService.ask("How many players can play uno?");

    Assertions.assertThat(answer)
        .isEqualTo("We don't talk about UNO. No no no... We don't talk about UNO. But...");

  }

}
