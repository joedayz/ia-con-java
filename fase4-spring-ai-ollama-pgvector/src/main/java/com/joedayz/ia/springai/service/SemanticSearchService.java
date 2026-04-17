package com.joedayz.ia.springai.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.joedayz.ia.springai.dto.BuscarResponse;

/**
 * Búsqueda semántica persistente con PgVector usando embeddings de Ollama.
 *
 * A diferencia de Fase 3 (SimpleVectorStore en memoria), esta versión usa
 * PgVectorStore que persiste los embeddings en PostgreSQL. Los documentos
 * indexados sobreviven reinicios de la aplicación.
 */
@Service
public class SemanticSearchService {

    private static final int MARKDOWN_CHUNK_SIZE = 700;
    private static final int MARKDOWN_CHUNK_OVERLAP = 120;

    private final VectorStore vectorStore;

    public SemanticSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        System.out.println("✅ SemanticSearchService inicializado con " + vectorStore.getClass().getSimpleName());
    }

    public int cargarDocumentosDemo() {
        List<Document> documentos = new ArrayList<>();
        documentos.add(new Document(
                "Embeddings: un texto se transforma en un vector numerico que captura su significado semantico.",
                Map.of("tema", "embeddings", "fuente", "teoria")));
        documentos.add(new Document(
                "Similitud coseno: mide el angulo entre dos vectores; 1.0 indica direcciones muy parecidas.",
                Map.of("tema", "similitud-coseno", "fuente", "teoria")));
        documentos.add(new Document(
                "Modelos de embedding convierten frases en vectores para comparar cercania semantica.",
                Map.of("tema", "embedding-model", "fuente", "teoria")));
        documentos.add(new Document(
                "PgVector es una extension de PostgreSQL para almacenar y buscar vectores de embeddings con indices HNSW.",
                Map.of("tema", "pgvector", "fuente", "teoria")));
        documentos.add(new Document(
                "Ollama permite ejecutar modelos locales de chat y embeddings, util para demos privadas y sin API key externa.",
                Map.of("tema", "ollama", "fuente", "teoria")));
        documentos.add(new Document(
                "RAG (Retrieval Augmented Generation) combina busqueda semantica con generacion de texto para respuestas contextuales.",
                Map.of("tema", "rag", "fuente", "teoria")));

        vectorStore.add(documentos);
        return documentos.size();
    }

    public BuscarResponse buscar(String query, int topK) {
        List<Document> similares = buscarDocumentos(query, topK);

        List<BuscarResponse.ResultadoDocumento> resultados = similares.stream()
                .map(doc -> new BuscarResponse.ResultadoDocumento(doc.getText(), doc.getMetadata()))
                .toList();

        return new BuscarResponse(query, resultados);
    }

    public List<Document> buscarDocumentos(String query, int topK) {
        int safeTopK = Math.max(1, Math.min(topK, 20));
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(safeTopK).build());
    }

    public int cargarPdf(String path, String sourceId) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("No se encontró el archivo PDF: " + path);
        }

        TikaDocumentReader reader = new TikaDocumentReader(new FileSystemResource(file));
        List<Document> docs = reader.get();

        if (docs.isEmpty()) {
            return 0;
        }

        String source = (sourceId == null || sourceId.isBlank()) ? file.getName() : sourceId;
        List<Document> marcados = docs.stream()
                .map(doc -> new Document(doc.getText(), mergeMetadata(doc.getMetadata(), source, path)))
                .toList();

        vectorStore.add(marcados);
        return marcados.size();
    }

    public Map<String, Object> getStatus() {
        return Map.of(
                "vectorStoreReady", true,
                "vectorStoreType", vectorStore.getClass().getSimpleName(),
                "persistent", true,
                "tip", "PgVector persiste los documentos. No se pierden al reiniciar.");
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }

    public int cargarMarkdown(String path, String sourceId) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("No se encontró el archivo: " + path);
        }

        String contenido;
        try {
            contenido = java.nio.file.Files.readString(file.toPath());
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo: " + path, e);
        }

        if (contenido.isBlank()) {
            return 0;
        }

        String source = (sourceId == null || sourceId.isBlank()) ? file.getName() : sourceId;
        List<Document> docs = splitMarkdownIntoChunks(contenido, source, path);

        if (docs.isEmpty()) {
            return 0;
        }

        vectorStore.add(docs);
        return docs.size();
    }

    private List<Document> splitMarkdownIntoChunks(String contenido, String source, String path) {
        String[] secciones = contenido.split("(?=^#{1,3} )", java.util.regex.Pattern.MULTILINE);
        List<Document> docs = new ArrayList<>();

        for (String seccion : secciones) {
            String texto = seccion.strip();
            if (texto.length() < 30) {
                continue;
            }
            String titulo = "sin-titulo";
            int newline = texto.indexOf('\n');
            if (newline > 0) {
                titulo = texto.substring(0, newline).replaceAll("^#+\\s*", "").strip();
            }

            List<String> chunks = splitTextWithOverlap(texto, MARKDOWN_CHUNK_SIZE, MARKDOWN_CHUNK_OVERLAP);
            for (int i = 0; i < chunks.size(); i++) {
                docs.add(new Document(chunks.get(i), Map.of(
                        "fuente", source,
                        "seccion", titulo,
                        "filePath", path,
                        "tipo", "markdown",
                        "chunk", i + 1,
                        "totalChunks", chunks.size())));
            }
        }
        return docs;
    }

    private List<String> splitTextWithOverlap(String text, int chunkSize, int overlap) {
        if (text.length() <= chunkSize) {
            return List.of(text);
        }

        List<String> chunks = new ArrayList<>();
        int step = Math.max(1, chunkSize - overlap);
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end).strip();
            if (chunk.length() >= 30) {
                chunks.add(chunk);
            }
            if (end == text.length()) {
                break;
            }
            start += step;
        }

        return chunks.isEmpty() ? List.of(text.substring(0, Math.min(text.length(), chunkSize)).strip()) : chunks;
    }

    private Map<String, Object> mergeMetadata(Map<String, Object> metadata, String source, String path) {
        Map<String, Object> result = new java.util.HashMap<>();
        if (metadata != null) {
            result.putAll(metadata);
        }
        result.put("fuente", source);
        result.put("pdfPath", path);
        return result;
    }
}
