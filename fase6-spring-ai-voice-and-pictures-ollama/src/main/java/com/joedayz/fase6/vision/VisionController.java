package com.joedayz.fase6.vision;

import java.util.Locale;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vision")
@Tag(name = "Vision", description = "Análisis de imágenes con modelo vision de Ollama vía Spring AI")
public class VisionController {

    private final ChatClient chatClient;

    public VisionController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Analiza una imagen subida y responde a la pregunta sobre ella.
     */
    @Operation(summary = "Describir / analizar imagen",
               description = "Sube una imagen y haz una pregunta sobre ella. Ollama vision la analizará y responderá.")
    @PostMapping(value = "/describe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VisionResponse describe(
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "question", required = false) String question) {

        String userQuestion = (question != null && !question.isBlank())
                ? question
                : "Describe esta imagen en detalle.";

        MimeType mimeType = resolveImageMimeType(image);

        String answer = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(userQuestion)
                        .media(mimeType, image.getResource()))
                .call()
                .content();

        return new VisionResponse(answer, userQuestion);
    }

    /** MIME coherente para Ollama: si el cliente manda octet-stream o vacío, inferimos por extensión. */
    private static MimeType resolveImageMimeType(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType != null && !contentType.isBlank()
                && !MediaType.APPLICATION_OCTET_STREAM_VALUE.equalsIgnoreCase(contentType)) {
            return MimeTypeUtils.parseMimeType(contentType);
        }
        String name = image.getOriginalFilename();
        if (name != null) {
            String lower = name.toLowerCase(Locale.ROOT);
            if (lower.endsWith(".png")) {
                return MimeTypeUtils.parseMimeType(MediaType.IMAGE_PNG_VALUE);
            }
            if (lower.endsWith(".gif")) {
                return MimeTypeUtils.parseMimeType(MediaType.IMAGE_GIF_VALUE);
            }
            if (lower.endsWith(".webp")) {
                return MimeTypeUtils.parseMimeType("image/webp");
            }
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                return MimeTypeUtils.parseMimeType(MediaType.IMAGE_JPEG_VALUE);
            }
        }
        return MimeTypeUtils.parseMimeType(MediaType.IMAGE_JPEG_VALUE);
    }

    public record VisionResponse(String description, String question) {}
}
