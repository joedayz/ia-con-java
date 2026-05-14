# Spring AI: Guía de Referencia Rápida

## ¿Qué es Spring AI?

Spring AI es el módulo oficial de Spring Framework para integrar modelos de inteligencia artificial generativa en aplicaciones Java. Actualmente en versión 1.0.x, proporciona abstracciones uniformes para trabajar con múltiples proveedores de IA.

## Componentes principales

### ChatClient
El componente central para interactuar con LLMs. Usa un builder fluent:

```java
ChatClient chatClient = ChatClient.builder(chatModel).build();

String respuesta = chatClient.prompt()
    .system("Eres un asistente experto en Java")
    .user("¿Qué es Spring Boot?")
    .call()
    .content();
```

### ChatModel
Interfaz que abstrae la comunicación con el proveedor de IA. Cada proveedor tiene su implementación: OpenAiChatModel, AnthropicChatModel, VertexAiGeminiChatModel.

### EmbeddingModel
Genera vectores numéricos (embeddings) a partir de texto. Se usa para búsqueda semántica y RAG.

### VectorStore
Almacena y busca embeddings. Implementaciones disponibles:
- SimpleVectorStore (en memoria)
- PgVectorStore (PostgreSQL)
- ChromaVectorStore (Chroma DB)

### Advisors
Los advisors interceptan y enriquecen las llamadas al ChatClient:
- **MessageChatMemoryAdvisor**: añade historial de conversación automáticamente.
- **QuestionAnswerAdvisor**: implementa RAG automáticamente buscando en un VectorStore y agregando el contexto al prompt.

## Configuración por perfil

Spring AI se configura en application.yml con perfiles:

```yaml
# application-openai.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat.options.model: gpt-3.5-turbo
```

Para cambiar de proveedor, solo cambia el perfil activo:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=openai
mvn spring-boot:run -Dspring-boot.run.profiles=anthropic
mvn spring-boot:run -Dspring-boot.run.profiles=vertex
```

## Flujo típico con Spring AI

1. **Configurar**: API keys en variables de entorno, perfil activo.
2. **Inyectar**: Spring auto-configura ChatModel y EmbeddingModel.
3. **Construir**: Crear ChatClient con ChatClient.builder().
4. **Usar**: Llamar prompt().system().user().call().content().
5. **Extender**: Agregar advisors para memoria, RAG, logging.

## Chat Memory

Spring AI soporta memoria conversacional con ChatMemory:

```java
@Bean
ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(new InMemoryChatMemoryRepository())
        .maxMessages(20)
        .build();
}
```

Se integra con ChatClient usando MessageChatMemoryAdvisor:
```java
chatClient.prompt()
    .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
        .conversationId("session-123")
        .build())
    .user("Hola, me llamo Carlos")
    .call().content();
```

## Document Readers

Spring AI puede leer diferentes formatos de documentos:
- **TikaDocumentReader**: PDF, Word, HTML y más via Apache Tika.
- **TextReader**: archivos de texto plano.
- **JsonReader**: documentos JSON estructurados.

Ejemplo con Tika:
```java
TikaDocumentReader reader = new TikaDocumentReader(new FileSystemResource("doc.pdf"));
List<Document> docs = reader.get();
vectorStore.add(docs);
```
