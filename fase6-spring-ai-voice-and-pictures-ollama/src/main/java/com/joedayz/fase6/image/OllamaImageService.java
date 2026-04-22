package com.joedayz.fase6.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class OllamaImageService implements ImageService {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaImageService.class);

    private final ChatClient chatClient;

    public OllamaImageService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generateImageUrl(String prompt) {
        LOG.info("Generando imagen (data URL) con Ollama para prompt: {}", prompt);
        byte[] pngBytes = generateImageBytes(prompt);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngBytes);
    }

    @Override
    public byte[] generateImageBytes(String prompt) {
        LOG.info("Generando imagen (PNG) con Ollama para prompt: {}", prompt);
        String svg = askSvgToModel(prompt);
        return svgToPng(svg);
    }

    private String askSvgToModel(String prompt) {
        String answer = chatClient.prompt()
                .user("""
                        Genera SOLO un SVG valido (sin markdown, sin comentarios) de 1024x1024.
                        Debe representar esta escena: %s
                        Restricciones:
                        - Responde solo con XML SVG.
                        - Incluye elementos visuales claros y colores.
                        - No incluyas texto fuera del propio SVG.
                        """.formatted(prompt))
                .call()
                .content();

        if (answer == null || answer.isBlank()) {
            return fallbackSvg(prompt);
        }
        return sanitizeSvg(answer);
    }

    private String sanitizeSvg(String raw) {
        String cleaned = raw.trim()
                .replace("```svg", "")
                .replace("```", "")
                .trim();
        if (!cleaned.startsWith("<svg")) {
            return fallbackSvg("Ilustracion minimalista");
        }
        return cleaned;
    }

    private byte[] svgToPng(String svg) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PNGTranscoder transcoder = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(
                    new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8)));
            TranscoderOutput output = new TranscoderOutput(out);
            transcoder.transcode(input, output);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible convertir SVG a PNG", e);
        }
    }

    private String fallbackSvg(String prompt) {
        String safePrompt = prompt.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return """
                <svg xmlns="http://www.w3.org/2000/svg" width="1024" height="1024" viewBox="0 0 1024 1024">
                    <defs>
                        <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
                            <stop offset="0%%" stop-color="#16213e"/>
                            <stop offset="100%%" stop-color="#1f4068"/>
                        </linearGradient>
                    </defs>
                    <rect width="1024" height="1024" fill="url(#bg)"/>
                    <circle cx="512" cy="420" r="180" fill="#e43f5a" opacity="0.85"/>
                    <rect x="200" y="640" width="624" height="180" rx="24" fill="#ffffff" opacity="0.15"/>
                    <text x="512" y="720" text-anchor="middle" font-size="36" fill="#ffffff" font-family="Arial, sans-serif">
                        %s
                    </text>
                </svg>
                """.formatted(safePrompt);
    }
}
