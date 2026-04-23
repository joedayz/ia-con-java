

package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenAiVoiceService implements VoiceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiVoiceService.class);

  private final OpenAiAudioTranscriptionModel transcriptionModel;
  
  private final SpeechModel speechModel;
  

  
  
  /*
  
  public OpenAiVoiceService(
      OpenAiAudioTranscriptionModel transcriptionModel) {
    this.transcriptionModel = transcriptionModel; 
  }
  
  */


  public OpenAiVoiceService(
      OpenAiAudioTranscriptionModel transcriptionModel,
      SpeechModel speechModel) {
    this.transcriptionModel = transcriptionModel;
    this.speechModel = speechModel;
  }
  

  /*
  

  ...


   */

  

  @Override
  public String transcribe(Resource audioFileResource) {
    var transcription = transcriptionModel.call(audioFileResource);
    if (transcription == null || transcription.isBlank()) {
      LOGGER.warn("Received empty transcription from speech-to-text model.");
      return "";
    }

    var preview = transcription.replaceAll("\\s+", " ").trim();
    if (preview.length() > 120) {
      preview = preview.substring(0, 120) + "...";
    }
    LOGGER.info("Transcription ok ({} chars): {}", transcription.length(), preview);
    return transcription;
  }

  

  /*

  
  @Override
  public Resource textToSpeech(String text) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  
  */

  
  @Override
  public Resource textToSpeech(String text) {
    var speechBytes = speechModel.call(text);
    return new ByteArrayResource(speechBytes);
  }
  

  

}



