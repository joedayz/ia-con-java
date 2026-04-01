package com.joedayz.ia.springai.service;

import com.joedayz.ia.springai.dto.BuscarResponse;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Labs 9 y 10 (START): busqueda semantica con embeddings y SimpleVectorStore.
 *
 * TODOs sugeridos para clase:
 * 1) Crear/inyectar EmbeddingModel.
 * 2) Crear SimpleVectorStore en memoria.
 * 3) Indexar documentos teoricos (embeddings, coseno, modelos, PgVector, Chroma).
 * 4) Implementar similarity search por query.
 * 5) Reto: cargar PDF con TikaDocumentReader e indexarlo.
 */
@Service
public class SemanticSearchService {

    public int cargarDocumentosDemo() {
        // TODO LAB 9: Indexar textos semanticos con SimpleVectorStore.
        throw new UnsupportedOperationException("TODO LAB 9: Implementar carga demo en vector store");
    }

    public BuscarResponse buscar(String query, int topK) {
        // TODO LAB 10: Ejecutar similaritySearch(query, topK).
        throw new UnsupportedOperationException("TODO LAB 10: Implementar endpoint /buscar");
    }

    public List<Document> buscarDocumentos(String query, int topK) {
        // TODO RAG: Reutilizar la busqueda semantica para obtener Document crudo.
        throw new UnsupportedOperationException("TODO RAG: Implementar recuperacion de documentos para /api/rag");
    }

    public int cargarPdf(String path, String sourceId) {
        // TODO RETO: Usar TikaDocumentReader, dividir en documentos e indexar embeddings.
        throw new UnsupportedOperationException("TODO RETO: Implementar carga de PDF con TikaDocumentReader");
    }
}

