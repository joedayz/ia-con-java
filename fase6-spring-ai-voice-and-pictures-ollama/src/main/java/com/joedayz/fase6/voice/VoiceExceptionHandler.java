package com.joedayz.fase6.voice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VoiceExceptionHandler {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(SttUnavailableException.class)
    public ErrorResponse handleSttUnavailable(SttUnavailableException ex) {
        return new ErrorResponse(
                "STT_UNAVAILABLE",
                ex.getMessage(),
                "Instala whisper CLI o configura APP_VOICE_STT_COMMAND con ruta absoluta.");
    }

    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler(SttTimeoutException.class)
    public ErrorResponse handleSttTimeout(SttTimeoutException ex) {
        return new ErrorResponse(
                "STT_TIMEOUT",
                ex.getMessage(),
                "Usa audios mas cortos o reduce el modelo con APP_VOICE_STT_MODEL=tiny.");
    }

    public record ErrorResponse(String error, String message, String hint) {}
}

