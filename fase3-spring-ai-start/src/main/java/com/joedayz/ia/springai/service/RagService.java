package com.joedayz.ia.springai.service;

import com.joedayz.ia.springai.dto.RagRequest;
import com.joedayz.ia.springai.dto.RagResponse;
import org.springframework.stereotype.Service;

/**
 * RAG (START): recuperar fragmentos, meterlos en el prompt y generar respuesta.
 *
 * TODOs sugeridos:
 * 1) Recuperar topK documentos desde SemanticSearchService.
 * 2) Construir un prompt con contexto enumerado [1], [2], [3].
 * 3) Invocar ChatClient/ChatModel para generar la respuesta.
 * 4) Devolver citas y metadata en RagResponse.
 */
@Service
public class RagService {

    public RagResponse answer(RagRequest request) {
        // TODO LAB RAG:
        // - Validar query
        // - Recuperar contexto semantico
        // - Inyectarlo en el prompt
        // - Pedir al modelo que cite [1], [2], [3]
        throw new UnsupportedOperationException("TODO RAG: Implementar pipeline completo retrieval -> prompt -> generation");
    }
}

