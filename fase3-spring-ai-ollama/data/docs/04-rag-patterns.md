# RAG: Retrieval Augmented Generation

## ¿Qué es RAG?

RAG (Retrieval Augmented Generation) es un patrón de arquitectura que mejora las respuestas de un LLM proporcionándole contexto relevante recuperado de una base de conocimiento externa. En lugar de depender solo del conocimiento entrenado del modelo, RAG inyecta información actualizada y específica en el prompt.

## ¿Por qué necesitamos RAG?

Los LLM tienen limitaciones importantes:
- **Conocimiento desactualizado**: solo saben lo que aprendieron durante el entrenamiento.
- **Alucinaciones**: pueden inventar respuestas que suenan plausibles pero son falsas.
- **Sin datos privados**: no conocen la documentación interna de tu empresa.

RAG resuelve estos problemas al recuperar datos reales antes de generar una respuesta.

## Pipeline de RAG paso a paso

### 1. Indexación (offline)
- Se recopilan los documentos fuente (PDFs, Markdown, bases de datos).
- Se dividen en chunks (fragmentos) de tamaño manejable.
- Cada chunk se convierte en un embedding usando un modelo de embeddings.
- Los embeddings se almacenan en una base de datos vectorial junto con el texto original.

### 2. Recuperación (retrieval)
- El usuario hace una pregunta.
- La pregunta se convierte en un embedding.
- Se buscan los K chunks más similares en la base vectorial (top-K similarity search).
- Se obtienen los fragmentos de texto más relevantes.

### 3. Generación (generation)
- Se construye un prompt que incluye: instrucciones del sistema + contexto recuperado + pregunta del usuario.
- Se envía al LLM.
- El LLM genera una respuesta basada en el contexto proporcionado.
- Opcionalmente se incluyen citas a los fragmentos usados.

## RAG Simple vs RAG con Advisor

### RAG Simple (Lab 11)
En el enfoque simple, el desarrollador controla todo el pipeline manualmente:

```java
// 1. Recuperar documentos
List<Document> docs = vectorStore.similaritySearch(query, topK);

// 2. Formatear contexto
String context = formatearComoTexto(docs);

// 3. Construir prompt manualmente
String respuesta = chatClient.prompt()
    .system("Responde solo con base en el contexto...")
    .user("Contexto: " + context + "\nPregunta: " + pregunta)
    .call().content();
```

### RAG con QuestionAnswerAdvisor (Lab 12)
Spring AI ofrece QuestionAnswerAdvisor que automatiza el pipeline:

```java
String respuesta = chatClient.prompt()
    .advisors(new QuestionAnswerAdvisor(vectorStore, 
        SearchRequest.builder().topK(4).build()))
    .user(pregunta)
    .call().content();
```

El advisor automáticamente busca en el VectorStore, formatea el contexto y lo inyecta en el prompt. Mucho menos código y más mantenible.

## Mejores prácticas

- **Chunk size**: entre 200 y 1000 tokens por fragmento. Muy cortos pierden contexto, muy largos diluyen la relevancia.
- **Overlap**: dejar solapamiento entre chunks (20-50 tokens) para no cortar ideas a la mitad.
- **Top-K**: empezar con 3-5 resultados. Más resultados pueden agregar ruido.
- **Prompts claros**: indicar al modelo que responda solo con la información del contexto.
- **Citas**: incluir referencias [1], [2] para que la respuesta sea verificable.

## Cuándo usar RAG

| Escenario | ¿RAG? | Alternativa |
|-----------|-------|-------------|
| Preguntas sobre docs internos | ✅ Sí | Fine-tuning (costoso) |
| Datos que cambian frecuentemente | ✅ Sí | Re-entrenar modelo |
| Chatbot de soporte técnico | ✅ Sí | - |
| Generación creativa libre | ❌ No | Prompt directo |
| Cálculos matemáticos | ❌ No | Tool calling |
