package com.joedayz.fase6.image;

public interface ImageService {

    /**
     * Genera una imagen a partir del prompt y devuelve la URL pública.
     */
    String generateImageUrl(String prompt);

    /**
     * Genera una imagen a partir del prompt y devuelve los bytes PNG.
     */
    byte[] generateImageBytes(String prompt);
}
