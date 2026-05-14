package com.joedayz.fase6.voice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice")
@Tag(name = "Voice", description = "Text-to-Speech y Speech-to-Text con OpenAI")
public class VoiceController {

    private final VoiceService voiceService;

    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    /**
     * Convierte texto a voz y devuelve el audio en formato MP3.
     *
     * Voces disponibles: alloy, echo, fable, onyx, nova, shimmer
     */
    @Operation(summary = "Text-to-Speech",
               description = "Convierte texto a audio MP3. Voces disponibles: alloy, echo, fable, onyx, nova, shimmer")
    @PostMapping(value = "/tts", produces = "audio/mpeg")
    public ResponseEntity<Resource> textToSpeech(
            @RequestParam String text,
            @RequestParam(defaultValue = "alloy") String voice) {

        Resource audioResource = voiceService.textToSpeech(text, voice);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.mp3\"")
                .body(audioResource);
    }

    /**
     * Transcribe un archivo de audio a texto usando Whisper.
     */
    @Operation(summary = "Speech-to-Text (Transcripción)",
               description = "Transcribe un archivo de audio (mp3, wav, m4a, etc.) a texto usando OpenAI Whisper")
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TranscriptionResponse transcribe(
            @RequestPart("audio") MultipartFile audioFile) {

        String transcription = voiceService.transcribe(audioFile.getResource());
        return new TranscriptionResponse(transcription);
    }

    public record TranscriptionResponse(String transcription) {}
}
