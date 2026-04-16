package com.joedayz.ia.springai.dto;

/**
 * DTO para cargar un PDF al vector store (Reto).
 */
public class CargarPdfRequest {
    private String path;
    private String sourceId;

    public CargarPdfRequest() {
    }

    public CargarPdfRequest(String path, String sourceId) {
        this.path = path;
        this.sourceId = sourceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}

