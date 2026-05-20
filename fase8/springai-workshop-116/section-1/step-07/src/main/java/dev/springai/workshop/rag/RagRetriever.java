package dev.springai.workshop.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retrieval + augmentation explícitos (equivalente a {@code RagRetriever} + {@code DefaultRetrievalAugmentor} en Quarkus).
 */
@Component
public class RagRetriever {

    private final VectorStore vectorStore;
    private final int maxResults;

    public RagRetriever(VectorStore vectorStore, @Value("${app.rag.max-results:3}") int maxResults) {
        this.vectorStore = vectorStore;
        this.maxResults = maxResults;
    }

    /**
     * Recupera segmentos relevantes e inyecta el contexto en el mensaje del usuario
     * con el formato personalizado del workshop.
     */
    public String augmentUserMessage(String userQuery) {
        List<Document> segments = vectorStore.similaritySearch(
                SearchRequest.builder().query(userQuery).topK(maxResults).build());

        StringBuilder prompt = new StringBuilder(userQuery);
        prompt.append("\nPlease, only use the following information:\n");
        for (Document segment : segments) {
            prompt.append("- ").append(segment.getText()).append('\n');
        }
        return prompt.toString();
    }
}
