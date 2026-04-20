package com.joedayz.ia.fase5.mcp.consumer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.joedayz.ia.fase5.mcp.consumer.service.MultimodalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MultimodalController.class)
class MultimodalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MultimodalService multimodalService;

    @Test
    void analyzeReturnsAnswerWhenImageIsPresent() throws Exception {
        when(multimodalService.analyzeImage(any(), eq("Que ves?")))
            .thenReturn("Veo un tablero con notas.");

        MockMultipartFile image = new MockMultipartFile(
            "image",
            "foto.png",
            "image/png",
            "fake-image".getBytes()
        );

        mockMvc.perform(multipart("/api/multimodal/analyze")
                .file(image)
                .param("prompt", "Que ves?"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.answer").value("Veo un tablero con notas."));

        verify(multimodalService).analyzeImage(any(), eq("Que ves?"));
    }

    @Test
    void analyzeReturnsBadRequestWhenImageIsMissing() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "empty.png",
            "image/png",
            new byte[0]
        );

        mockMvc.perform(multipart("/api/multimodal/analyze")
                .file(image))
            .andExpect(status().isBadRequest());
    }
}

