package com.joedayz.ia.springai.dto;

import java.util.List;
import java.util.Map;

/**
 * Response del endpoint RAG con respuesta generada y citas del contexto.
 */
public class RagResponse {
    private String question;
    private String answer;
    private int topK;
    private boolean contextUsed;
    private List<Citation> citations;

    public RagResponse() {
    }

    public RagResponse(String question, String answer, int topK, boolean contextUsed, List<Citation> citations) {
        this.question = question;
        this.answer = answer;
        this.topK = topK;
        this.contextUsed = contextUsed;
        this.citations = citations;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public boolean isContextUsed() {
        return contextUsed;
    }

    public void setContextUsed(boolean contextUsed) {
        this.contextUsed = contextUsed;
    }

    public List<Citation> getCitations() {
        return citations;
    }

    public void setCitations(List<Citation> citations) {
        this.citations = citations;
    }

    public static class Citation {
        private int index;
        private String excerpt;
        private Map<String, Object> metadata;

        public Citation() {
        }

        public Citation(int index, String excerpt, Map<String, Object> metadata) {
            this.index = index;
            this.excerpt = excerpt;
            this.metadata = metadata;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getExcerpt() {
            return excerpt;
        }

        public void setExcerpt(String excerpt) {
            this.excerpt = excerpt;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }
}

