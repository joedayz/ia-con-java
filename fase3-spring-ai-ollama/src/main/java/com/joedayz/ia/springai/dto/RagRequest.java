package com.joedayz.ia.springai.dto;

/**
 * Request para consultas RAG.
 */
public class RagRequest {
    private String query;
    private Integer topK;

    public RagRequest() {
    }

    public RagRequest(String query, Integer topK) {
        this.query = query;
        this.topK = topK;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }
}

