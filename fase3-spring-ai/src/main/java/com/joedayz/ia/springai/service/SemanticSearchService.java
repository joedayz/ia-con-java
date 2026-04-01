package com.joedayz.ia.springai.service;

import com.joedayz.ia.springai.dto.BuscarResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lab 9/10: Busqueda semantica en memoria con SimpleVectorStore.
 */
@Service
public class SemanticSearchService {

    private final EmbeddingModel embeddingModel;
    private VectorStore vectorStore;
    private boolean demoDocumentosCargados;

    public SemanticSearchService(ObjectProvider<EmbeddingModel> embeddingModelProvider) {
        this.embeddingModel = embeddingModelProvider.getIfAvailable();
    }

    public synchronized int cargarDocumentosDemo() {
        VectorStore store = requireVectorStore();
        if (demoDocumentosCargados) {
            return 0;
        }
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
                "SimpleVectorStore guarda embeddings en memoria. Es ideal para demos y pruebas rapidas.",
                Map.of("tema", "simplevectorstore", "fuente", "teoria")));
        documentos.add(new Document(
                "PgVector y Chroma son opciones de bases vectoriales para persistencia y escalabilidad.",
                Map.of("tema", "vector-database", "fuente", "teoria")));

        store.add(documentos);
        demoDocumentosCargados = true;
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
        VectorStore store = requireVectorStore();
        int safeTopK = Math.max(1, Math.min(topK, 20));
        return store.similaritySearch(
                SearchRequest.builder().query(query).topK(safeTopK).build());
    }

    public int cargarPdf(String path, String sourceId) {
        VectorStore store = requireVectorStore();

        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("No se encontro el archivo PDF: " + path);
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

        store.add(marcados);
        return marcados.size();
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

    private synchronized VectorStore requireVectorStore() {
        if (embeddingModel == null) {
            throw new IllegalStateException(
                    "No hay EmbeddingModel disponible para busqueda semantica. " +
                            "Usa perfil openai o vertex y configura sus credenciales.");
        }

        if (vectorStore == null) {
            vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        }

        return vectorStore;
    }
}

