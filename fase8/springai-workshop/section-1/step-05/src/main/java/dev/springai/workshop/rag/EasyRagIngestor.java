package dev.springai.workshop.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Ingesta automática al arranque (equivalente a EasyRAG en Quarkus).
 */
@Component
public class EasyRagIngestor implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EasyRagIngestor.class);

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Value("${app.rag.path:classpath:rag/}")
    private String ragPath;

    @Value("${app.rag.max-segment-size:100}")
    private int maxSegmentSize;

    @Value("${app.rag.max-overlap-size:25}")
    private int maxOverlapSize;

    public EasyRagIngestor(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        String pattern = toClasspathPattern(ragPath);
        Resource[] resources = resourceResolver.getResources(pattern);

        if (resources.length == 0) {
            log.warn("No RAG documents found at pattern: {}", pattern);
            return;
        }

        List<Document> documents = new ArrayList<>();
        for (Resource resource : resources) {
            if (!resource.isReadable()) {
                continue;
            }
            TextReader reader = new TextReader(resource);
            reader.getCustomMetadata().put("source", resource.getFilename());
            documents.addAll(reader.get());
        }

        TokenTextSplitter splitter = new TokenTextSplitter(
                maxSegmentSize, maxOverlapSize, 5, 10_000, true);
        List<Document> segments = splitter.apply(documents);
        vectorStore.add(segments);

        log.info("Ingesting documents from path: {}", pattern);
        log.info("Ingested {} files as {} document segments", resources.length, segments.size());
    }

    private static String toClasspathPattern(String path) {
        String normalized = path.endsWith("/") ? path : path + "/";
        if (normalized.startsWith("classpath:")) {
            return normalized + "**/*";
        }
        return "classpath:" + normalized + "**/*";
    }
}
