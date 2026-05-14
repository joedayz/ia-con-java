package com.joedayz.fase6.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image")
@Tag(name = "Image Generation", description = "Generación de imágenes con DALL-E vía Spring AI")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Genera una imagen y devuelve la URL pública de OpenAI.
     */
    @Operation(summary = "Generar imagen (URL)",
               description = "Genera una imagen 1024x1024 usando DALL-E y devuelve la URL temporal de OpenAI")
    @PostMapping("/generate-url")
    public ImageUrlResponse generateUrl(@RequestParam String prompt) {
        String url = imageService.generateImageUrl(prompt);
        return new ImageUrlResponse(url, prompt);
    }

    /**
     * Genera una imagen y la devuelve directamente como bytes PNG.
     */
    @Operation(summary = "Generar imagen (PNG)",
               description = "Genera una imagen 1024x1024 usando DALL-E y devuelve los bytes PNG directamente")
    @PostMapping(value = "/generate-png", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generatePng(@RequestParam String prompt) {
        return imageService.generateImageBytes(prompt);
    }

    public record ImageUrlResponse(String url, String prompt) {}
}
