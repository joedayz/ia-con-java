package com.joedayz.fase6.voice;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenAiVoiceService implements VoiceService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final SpeechModel speechModel;

    public OpenAiVoiceService(OpenAiAudioTranscriptionModel transcriptionModel,
                               SpeechModel speechModel) {
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
    }

    @Override
    public Resource textToSpeech(String text, String voice) {
        var voiceEnum = parseVoice(voice);
        var options = org.springframework.ai.openai.OpenAiAudioSpeechOptions.builder()
                .voice(voiceEnum)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();
        var speechBytes = speechModel.call(new SpeechPrompt(text, options)).getResult().getOutput();
        return new ByteArrayResource(speechBytes);
    }

    @Override
    public String transcribe(Resource audioResource) {
        return transcriptionModel.call(audioResource);
    }

    private OpenAiAudioApi.SpeechRequest.Voice parseVoice(String voice) {
        try {
            return OpenAiAudioApi.SpeechRequest.Voice.valueOf(voice.toUpperCase());
        } catch (Exception e) {
            return OpenAiAudioApi.SpeechRequest.Voice.ALLOY;
        }
    }
}
