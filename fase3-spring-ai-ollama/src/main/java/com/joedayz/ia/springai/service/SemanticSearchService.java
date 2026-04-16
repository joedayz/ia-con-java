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
 * Búsqueda semántica en memoria con SimpleVectorStore usando Ollama.
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
                "Ollama permite ejecutar modelos locales de chat y embeddings, util para demos privadas y sin API key externa.",
                Map.of("tema", "ollama", "fuente", "teoria")));

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
        return store.similaritySearch(SearchRequest.builder().query(query).topK(safeTopK).build());
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

    public Map<String, Object> getStatus() {
        if (embeddingModel == null) {
            return Map.of(
                    "vectorStoreReady", false,
                    "reason", "No hay EmbeddingModel disponible. Verifica la configuracion de Ollama y el modelo de embeddings.",
                    "documentsLoaded", 0
            );
        }

        if (vectorStore == null) {
            return Map.of(
                    "vectorStoreReady", true,
                    "documentsLoaded", 0,
                    "demoDocsLoaded", false,
                    "tip", "Ejecuta POST /api/buscar/demo o POST /api/buscar/pdf para indexar documentos"
            );
        }

        return Map.of(
                "vectorStoreReady", true,
                "documentsLoaded", "unknown",
                "demoDocsLoaded", demoDocumentosCargados,
                "tip", "SimpleVectorStore esta en memoria RAM. Se pierde al reiniciar."
        );
    }

    public synchronized VectorStore getVectorStore() {
        return requireVectorStore();
    }

    public int cargarMarkdown(String path, String sourceId) {
        VectorStore store = requireVectorStore();

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

        store.add(docs);
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
            if (texto.length() > 2000) {
                texto = texto.substring(0, 2000);
            }

            String titulo = "sin-titulo";
            int newline = texto.indexOf('\n');
            if (newline > 0) {
                titulo = texto.substring(0, newline).replaceAll("^#+\\s*", "").strip();
            }

            docs.add(new Document(texto, Map.of(
                    "fuente", source,
                    "seccion", titulo,
                    "filePath", path,
                    "tipo", "markdown"
            )));
        }
        return docs;
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
                    "No hay EmbeddingModel disponible para busqueda semantica. "
                            + "Asegurate de tener Ollama activo y el modelo de embeddings descargado.");
        }

        if (vectorStore == null) {
            vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        }

        return vectorStore;
    }
}

