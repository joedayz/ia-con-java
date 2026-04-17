# Bases de Datos Vectoriales

## ¿Qué son las bases vectoriales?

Una base de datos vectorial es un sistema de almacenamiento optimizado para guardar y buscar vectores (embeddings). A diferencia de las bases de datos relacionales que buscan por coincidencia exacta, las bases vectoriales buscan por similitud semántica usando métricas como similitud coseno o distancia euclidiana.

## SimpleVectorStore (desarrollo)

SimpleVectorStore es la implementación en memoria de Spring AI. Guarda todos los vectores en un HashMap de Java.

**Ventajas:**
- Sin configuración ni dependencias externas.
- Rapidísimo para pocos documentos.
- Ideal para demos, labs y prototipos.

**Desventajas:**
- Se pierde al reiniciar la aplicación.
- No escala a millones de documentos.
- No soporta filtros avanzados por metadata.

**Configuración en Spring AI:**
```java
VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
```

## PgVector (producción con PostgreSQL)

PgVector es una extensión de PostgreSQL que agrega soporte nativo para vectores. Permite almacenar embeddings junto con datos relacionales en la misma base de datos.

**Ventajas:**
- Persistencia real en disco.
- Integración con el ecosistema PostgreSQL (índices, transacciones, backups).
- Búsqueda por similitud con índices HNSW o IVFFlat.
- Filtrado por metadata usando SQL estándar.

**Desventajas:**
- Requiere instalar la extensión en PostgreSQL.
- Menos optimizado que bases vectoriales dedicadas para millones de vectores.

## Chroma (prototipos y despliegues ligeros)

Chroma es una base de datos vectorial open-source diseñada específicamente para aplicaciones con IA. Se ejecuta como un servidor independiente o embebido en Python.

**Ventajas:**
- Fácil de configurar y desplegar.
- API sencilla y bien documentada.
- Soporte nativo para metadata filtering.
- Persistencia en disco incluida.

**Desventajas:**
- Proyecto relativamente nuevo.
- Menos madurez empresarial que PostgreSQL.

## Pinecone (cloud managed)

Pinecone es un servicio cloud que ofrece base vectorial como servicio (DBaaS). No requiere infraestructura propia.

**Ventajas:**
- Totalmente gestionado, sin operaciones.
- Escalable automáticamente.
- Alta disponibilidad.

**Desventajas:**
- Servicio de pago.
- Dependencia del proveedor (vendor lock-in).
- Latencia de red al estar en la nube.

## Comparación rápida

| Base        | Tipo       | Persistencia | Escalabilidad | Costo       |
|-------------|-----------|-------------|---------------|-------------|
| SimpleVector| En memoria | No          | Baja          | Gratuito    |
| PgVector    | PostgreSQL | Sí          | Media-Alta    | Infra propia|
| Chroma      | Dedicada   | Sí          | Media         | Gratuito    |
| Pinecone    | Cloud      | Sí          | Alta          | Pago        |

## Recomendación por caso de uso

- **Labs y demos**: SimpleVectorStore
- **Producción con PostgreSQL existente**: PgVector
- **Prototipo rápido**: Chroma
- **Aplicación enterprise cloud**: Pinecone o PgVector en RDS
