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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ingesta explícita al arranque (equivalente a {@code RagIngestion} + {@code EmbeddingStoreIngestor} en Quarkus).
 */
@Component
public class RagIngestion implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RagIngestion.class);

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Value("${spring.ai.vectorstore.pgvector.table-name:vector_store}")
    private String pgVectorTable;

    @Value("${app.rag.location:classpath:rag/}")
    private String ragLocation;

    @Value("${app.rag.max-segment-size:100}")
    private int maxSegmentSize;

    @Value("${app.rag.max-overlap-size:25}")
    private int maxOverlapSize;

    public RagIngestion(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        String pattern = toClasspathPattern(ragLocation);
        Resource[] resources = resourceResolver.getResources(pattern);

        if (resources.length == 0) {
            log.warn("No documents found at {}", pattern);
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

        // PgVector: vaciar igual que EmbeddingStore.removeAll() en Quarkus step-06 (demo sin duplicados)
        truncateVectorTableSafely();
        vectorStore.add(segments);

        log.info("Documents ingested successfully ({} files, {} segments)", resources.length, segments.size());
    }

    /**
     * Nombre de tabla solo desde configuración Spring AI; validamos contra inyección SQL accidental.
     */
    private void truncateVectorTableSafely() {
        if (!pgVectorTable.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            throw new IllegalStateException("spring.ai.vectorstore.pgvector.table-name inválido: " + pgVectorTable);
        }
        jdbcTemplate.execute("TRUNCATE TABLE " + pgVectorTable + " RESTART IDENTITY");
    }

    private static String toClasspathPattern(String path) {
        String normalized = path.endsWith("/") ? path : path + "/";
        if (normalized.startsWith("classpath:")) {
            return normalized + "**/*";
        }
        return "classpath:" + normalized + "**/*";
    }
}
