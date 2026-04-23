
package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AskController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AskController.class);

  private final BoardGameService boardGameService;
  private final VoiceService voiceService;

  public AskController(BoardGameService boardGameService,
                       VoiceService voiceService) {  
    this.boardGameService = boardGameService;
    this.voiceService = voiceService;
  }
  

  /*
    

  // ...

    
   */


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
      @RequestParam("gameTitle") String gameTitle) {           

    LOGGER.info("audioAsk request gameTitle='{}', file='{}', contentType='{}', size={} bytes",
        gameTitle,
        audioBlob.getOriginalFilename(),
        audioBlob.getContentType(),
        audioBlob.getSize());

    var transcription = voiceService.transcribe(audioBlob.getResource());
    if (transcription.isBlank()) {
      return new Answer(gameTitle,
          "I could not transcribe the audio question. Please try again with clearer audio.");
    }

    var transcribedQuestion = new Question(gameTitle, transcription);
    return boardGameService.askQuestion(transcribedQuestion, conversationId);
  }
  

  
  @PostMapping(path="/audioAsk", produces = "audio/mpeg")
  public Resource audioAskAudioResponse(
      @RequestHeader(name="X_AI_CONVERSATION_ID",
          defaultValue = "default") String conversationId,
      @RequestParam("audio") MultipartFile blob,
      @RequestParam("gameTitle") String game) {

    LOGGER.info("audioAsk(audio response) gameTitle='{}', file='{}', contentType='{}', size={} bytes",
        game,
        blob.getOriginalFilename(),
        blob.getContentType(),
        blob.getSize());

    var transcription = voiceService.transcribe(blob.getResource());
    if (transcription.isBlank()) {
      return voiceService.textToSpeech(
          "I could not transcribe the audio question. Please try again with clearer audio.");
    }

    var transcribedQuestion = new Question(game, transcription);
    var answer = boardGameService.askQuestion(
            transcribedQuestion, conversationId);
    return voiceService.textToSpeech(answer.answer());
  }
  


  
}

