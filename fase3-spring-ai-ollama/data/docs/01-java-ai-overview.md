# IA con Java: Visión General

## ¿Por qué Java para IA?

Java sigue siendo uno de los lenguajes más utilizados en entornos empresariales. Con la llegada de Spring AI, los desarrolladores Java pueden integrar modelos de lenguaje (LLM) directamente en sus aplicaciones sin necesidad de cambiar de stack tecnológico. Java ofrece tipado fuerte, ecosistema maduro y herramientas de producción probadas como Spring Boot.

## Modelos de Lenguaje (LLM)

Un modelo de lenguaje grande (LLM) es una red neuronal entrenada con billones de tokens de texto. Ejemplos populares incluyen GPT-4 de OpenAI, Claude de Anthropic y Gemini de Google. Estos modelos pueden generar texto, responder preguntas, resumir documentos, traducir idiomas y más.

## Proveedores de IA soportados

En este curso trabajamos con tres proveedores principales:

- **OpenAI**: GPT-3.5-turbo y GPT-4. El más popular y con mejor documentación.
- **Anthropic**: Claude 3 Haiku, Sonnet y Opus. Destaca en instrucciones complejas y contextos largos.
- **Google Vertex AI**: Gemini Pro y Ultra. Integración nativa con Google Cloud.

## Arquitectura típica de una aplicación con IA

Una aplicación Java con IA generalmente sigue este patrón:

1. El usuario envía una petición HTTP al backend.
2. El backend construye un prompt con contexto y la pregunta del usuario.
3. Se envía el prompt al proveedor de IA vía API REST.
4. El modelo genera una respuesta.
5. El backend procesa la respuesta y la devuelve al frontend.

## Spring AI como framework

Spring AI es el framework oficial de Spring para integrar modelos de IA. Proporciona abstracciones como ChatClient, ChatModel, EmbeddingModel y VectorStore que simplifican la integración con múltiples proveedores. Su diseño sigue los principios de Spring: inversión de control, programación por interfaces y perfiles de configuración.
