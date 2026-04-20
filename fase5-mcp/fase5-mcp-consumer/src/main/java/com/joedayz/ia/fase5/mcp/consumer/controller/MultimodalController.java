package com.joedayz.ia.fase5.mcp.consumer.controller;

import com.joedayz.ia.fase5.mcp.consumer.dto.ChatResponse;
import com.joedayz.ia.fase5.mcp.consumer.service.MultimodalService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/multimodal")
public class MultimodalController {

    private final MultimodalService multimodalService;

    public MultimodalController(MultimodalService multimodalService) {
        this.multimodalService = multimodalService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChatResponse analyze(
        @RequestParam("image") MultipartFile image,
        @RequestParam(value = "prompt", required = false) String prompt
    ) {
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Debes enviar una imagen en el campo 'image'.");
        }

        return new ChatResponse(multimodalService.analyzeImage(image, prompt));
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of(
            "status", "running",
            "service", "multimodal",
            "configuredModel", multimodalService.getConfiguredModel(),
            "recommendedModel", multimodalService.getRecommendedModel(),
            "tip", "Para analizar imagenes, usa un modelo de vision en Ollama"
        );
    }
}
