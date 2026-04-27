package com.example.boardgamebuddy;

import org.springframework.ai.moderation.ModerationModel;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.openai.OpenAiModerationOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ModerationService {

  private final ModerationModel moderationModel;
  private final OpenAiModerationOptions moderationOptions;

  public ModerationService(
      ModerationModel moderationModel,
      @Value("${spring.ai.openai.moderation.options.model:omni-moderation-latest}") String moderationModelId) {
    this.moderationModel = moderationModel;
    this.moderationOptions = OpenAiModerationOptions.builder()
        .model(moderationModelId)
        .build();
  }

  public void moderate(String text) {
    var moderationResponse =
        moderationModel.call(new ModerationPrompt(text, moderationOptions));

    var moderationResult = moderationResponse.getResult()
        .getOutput().getResults().getFirst();
    var categories = moderationResult.getCategories();    

    if (categories.isHate() || categories.isHateThreatening()) 
      throw new ModerationException("Hate");
    else if (categories.isHarassment() ||
             categories.isHarassmentThreatening())      
      throw new ModerationException("Harassment");
    else if (categories.isViolence())                   
      throw new ModerationException("Violence");
  }

}
