package com.example.boardgamebuddy;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AskController {

  private final BoardGameService boardGameService;
  private final VoiceService voiceService;

  public AskController(BoardGameService boardGameService,
                       VoiceService voiceService) {
    this.boardGameService = boardGameService;
    this.voiceService = voiceService;
  }

  @PostMapping(path = "/ask", produces = "application/json")
  public Answer ask(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestBody Question question) {
    return boardGameService.askQuestion(question, conversationId);
  }

  @PostMapping(path="/audioAsk", produces = "application/json")
  public Answer audioAsk(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestParam("audio") MultipartFile audioBlob,
      @RequestParam("gameTitle") String game) {

    var transcription = voiceService.transcribe(audioBlob.getResource());
    var transcribedQuestion = new Question(game, transcription);
    return boardGameService.askQuestion(transcribedQuestion, conversationId);
  }

  @PostMapping(path="/audioAsk", produces = "audio/mpeg")
  public Resource audioAskAudioResponse(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestParam("audio") MultipartFile blob,
      @RequestParam("gameTitle") String game) {

    var transcription = voiceService.transcribe(blob.getResource());
    var transcribedQuestion = new Question(game, transcription);
    var answer = boardGameService.askQuestion(
            transcribedQuestion, conversationId);
    return voiceService.textToSpeech(answer.answer());
  }

  
  @PostMapping(path="/visionAsk",
               produces = "application/json",
               consumes = "multipart/form-data")
  public Answer visionAsk(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestPart("image") MultipartFile image, 
      @RequestPart("gameTitle") String game,
      @RequestPart("question") String questionIn) {

    var imageResource = image.getResource();    
    var imageContentType = image.getContentType(); 

    var question = new Question(game, questionIn);
    return boardGameService.askQuestion(
        question, imageResource, imageContentType, conversationId); 
  }
  

}
