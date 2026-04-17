-- Habilitar extensiones necesarias
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- La tabla vector_store será creada automáticamente por Spring AI
-- con initialize-schema=true en application.yml
