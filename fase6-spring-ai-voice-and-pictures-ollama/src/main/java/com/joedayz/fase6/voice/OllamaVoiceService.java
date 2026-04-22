package com.joedayz.fase6.voice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

@Service
public class OllamaVoiceService implements VoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaVoiceService.class);

    private final String ttsCommand;
    private final String defaultVoice;
    private final String sttCommand;
    private final String sttModel;
    private final String sttLanguage;
    private final String osName;

    public OllamaVoiceService(
            @Value("${app.voice.tts.command:say}") String ttsCommand,
            @Value("${app.voice.tts.default-voice:Monica}") String defaultVoice,
            @Value("${app.voice.stt.command:whisper}") String sttCommand,
            @Value("${app.voice.stt.model:base}") String sttModel,
            @Value("${app.voice.stt.language:es}") String sttLanguage) {
        this.ttsCommand = ttsCommand;
        this.defaultVoice = defaultVoice;
        this.sttCommand = sttCommand;
        this.sttModel = sttModel;
        this.sttLanguage = sttLanguage;
        this.osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
    }

    @Override
    public VoiceAudio textToSpeech(String text, String voice) {
        String chosenVoice = (voice == null || voice.isBlank()) ? defaultVoice : voice;
        if (osName.contains("mac")) {
            return textToSpeechMac(text, chosenVoice);
        }
        if (osName.contains("win")) {
            return textToSpeechWindows(text, chosenVoice);
        }
        throw new IllegalStateException("TTS local soporta macOS y Windows en esta demo.");
    }

    private VoiceAudio textToSpeechMac(String text, String voice) {
        try {
            Path outputAudio = Files.createTempFile("fase6-tts-", ".aiff");

            int exitCode = new ProcessBuilder(
                    ttsCommand,
                    "-v", voice,
                    "-o", outputAudio.toString(),
                    text)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start()
                    .waitFor();

            if (exitCode != 0 || !Files.exists(outputAudio)) {
                throw new IllegalStateException("No fue posible generar audio con comando local de TTS");
            }
            return new VoiceAudio(
                    new ByteArrayResource(Files.readAllBytes(outputAudio)),
                    "audio/aiff",
                    "speech.aiff");
        } catch (Exception e) {
            throw new IllegalStateException("Error ejecutando TTS local. Verifica comando 'say' en macOS.", e);
        }
    }

    private VoiceAudio textToSpeechWindows(String text, String voice) {
        try {
            Path outputAudio = Files.createTempFile("fase6-tts-", ".wav");
            String script = "Add-Type -AssemblyName System.Speech;"
                    + "$s=New-Object System.Speech.Synthesis.SpeechSynthesizer;"
                    + "try {$s.SelectVoice('" + escapePowerShellSingleQuoted(voice) + "')} catch {} ;"
                    + "$s.SetOutputToWaveFile('" + escapePowerShellSingleQuoted(outputAudio.toString()) + "');"
                    + "$s.Speak('" + escapePowerShellSingleQuoted(text) + "');"
                    + "$s.Dispose();";

            int exitCode = new ProcessBuilder(
                    "powershell",
                    "-NoProfile",
                    "-ExecutionPolicy", "Bypass",
                    "-Command", script)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start()
                    .waitFor();

            if (exitCode != 0 || !Files.exists(outputAudio)) {
                throw new IllegalStateException("No fue posible generar audio con PowerShell TTS");
            }
            return new VoiceAudio(
                    new ByteArrayResource(Files.readAllBytes(outputAudio)),
                    "audio/wav",
                    "speech.wav");
        } catch (Exception e) {
            throw new IllegalStateException("Error ejecutando TTS en Windows (PowerShell/System.Speech).", e);
        }
    }

    @Override
    public String transcribe(Resource audioResource) {
        Path tmpAudio = null;
        try {
            tmpAudio = Files.createTempFile("fase6-stt-", ".wav");
            Files.copy(audioResource.getInputStream(), tmpAudio, StandardCopyOption.REPLACE_EXISTING);

            Path outDir = Files.createTempDirectory("fase6-whisper-out-");
            int exitCode = new ProcessBuilder(
                    sttCommand,
                    tmpAudio.toString(),
                    "--model", sttModel,
                    "--language", sttLanguage,
                    "--output_format", "txt",
                    "--output_dir", outDir.toString())
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start()
                    .waitFor();

            String audioFilename = tmpAudio.getFileName().toString();
            int dotIndex = audioFilename.lastIndexOf('.');
            String stem = dotIndex > 0 ? audioFilename.substring(0, dotIndex) : audioFilename;
            Path transcript = outDir.resolve(stem + ".txt");
            if (exitCode != 0 || !Files.exists(transcript)) {
                throw new IllegalStateException("No fue posible transcribir audio con whisper CLI");
            }

            return Files.readString(transcript).trim();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error en STT local. Instala 'whisper' CLI o ajusta app.voice.stt.command.", e);
        } finally {
            if (tmpAudio != null) {
                try {
                    Files.deleteIfExists(tmpAudio);
                } catch (Exception ignored) {
                    LOG.debug("No se pudo limpiar audio temporal", ignored);
                }
            }
        }
    }

    private String escapePowerShellSingleQuoted(String input) {
        return input.replace("'", "''");
    }
}
