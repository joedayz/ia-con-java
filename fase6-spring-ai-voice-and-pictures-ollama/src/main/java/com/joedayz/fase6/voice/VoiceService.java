package com.joedayz.fase6.voice;

import org.springframework.core.io.Resource;

public interface VoiceService {

    record VoiceAudio(Resource resource, String contentType, String filename) {}

    /**
     * Convierte texto a audio con proveedor local.
     */
    VoiceAudio textToSpeech(String text, String voice);

    /**
     * Transcribe un archivo de audio a texto usando STT local.
     */
    String transcribe(Resource audioResource);
}
