# Embeddings: Representaciones Vectoriales del Texto

## ¿Qué es un embedding?

Un embedding es un vector numérico de dimensión fija que captura el significado semántico de un texto. Cuando un modelo de embeddings procesa una frase como "El gato duerme en el sofá", la transforma en un arreglo de números, por ejemplo un vector de 1536 dimensiones en el caso de OpenAI text-embedding-3-small.

## Propiedades clave de los embeddings

- **Determinismo**: el mismo texto siempre produce el mismo vector.
- **Similitud semántica**: textos con significados similares generan vectores que están más cerca entre sí en el espacio vectorial.
- **Dimensionalidad fija**: independientemente del largo del texto, el vector resultante tiene siempre la misma cantidad de dimensiones.

## Modelos de embeddings populares

### OpenAI
- `text-embedding-ada-002`: 1536 dimensiones. Modelo clásico, buena relación calidad/precio.
- `text-embedding-3-small`: 1536 dimensiones. Más nuevo y eficiente.
- `text-embedding-3-large`: 3072 dimensiones. Mayor precisión para casos exigentes.

### Open Source
- **Sentence Transformers** (Hugging Face): modelos como `all-MiniLM-L6-v2` con 384 dimensiones. Gratuitos y ejecutables localmente.
- **Cohere Embed v3**: alternativa comercial con soporte multilenguaje.
- **Ollama**: permite ejecutar modelos de embeddings localmente como `nomic-embed-text`.

## Similitud Coseno

La similitud coseno mide el ángulo entre dos vectores, no su magnitud. La fórmula es:

    coseno(A, B) = (A · B) / (||A|| × ||B||)

Donde A · B es el producto punto y ||A|| es la norma del vector.

| Rango     | Interpretación       |
|-----------|---------------------|
| 0.9 - 1.0 | Semánticamente idénticos |
| 0.7 - 0.9 | Muy similares       |
| 0.5 - 0.7 | Relacionados        |
| 0.2 - 0.5 | Vagamente relacionados |
| < 0.2     | No relacionados     |

## Uso práctico en Java con Spring AI

```java
EmbeddingModel model = ...; // Inyectado por Spring
float[] vector = model.embed("Hola mundo");
// vector tiene 1536 dimensiones (OpenAI)
```

Spring AI abstrae los detalles del proveedor. El mismo código funciona con OpenAI, Vertex AI u Ollama cambiando solo la configuración.
