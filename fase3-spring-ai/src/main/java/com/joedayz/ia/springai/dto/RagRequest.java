package com.joedayz.ia.springai.dto;

/**
 * Request del endpoint RAG.
 */
public class RagRequest {
    private String question;
    private Integer topK;

    public RagRequest() {
    }

    public RagRequest(String question, Integer topK) {
        this.question = question;
        this.topK = topK;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }
}

