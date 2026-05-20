package dev.springai.workshop.car.domain;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Imagen opcional adjunta en la devolución del vehículo (equiv. {@code ImageContent} en LangChain4j).
 */
public record CarImageInput(byte[] data, String mimeType) {

    public boolean hasImage() {
        return data != null && data.length > 0;
    }

    public Resource resource() {
        return new ByteArrayResource(data);
    }

    public static CarImageInput from(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        return new CarImageInput(file.getBytes(), contentType);
    }
}
