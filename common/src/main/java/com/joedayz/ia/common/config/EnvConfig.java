package com.joedayz.ia.common.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Carga y expone variables desde .env (raíz del repo o directorio actual).
 * Busca .env en el directorio de trabajo y, si no existe, en el padre (al ejecutar desde un submódulo).
 */
public final class EnvConfig {

    private static final Dotenv DOTENV = loadDotenv();

    private static Dotenv loadDotenv() {
        Path cwd = Paths.get("").toAbsolutePath();
        Path envInCwd = cwd.resolve(".env");
        Path envInParent = cwd.getParent() != null ? cwd.getParent().resolve(".env") : null;
        if (Files.exists(envInCwd)) {
            return Dotenv.configure().directory(cwd.toFile().getAbsolutePath()).ignoreIfMissing().load();
        }
        if (envInParent != null && Files.exists(envInParent)) {
            return Dotenv.configure().directory(envInParent.getParent().toFile().getAbsolutePath()).ignoreIfMissing().load();
        }
        return Dotenv.configure().ignoreIfMissing().load();
    }

    private EnvConfig() {}

    /** API Key de OpenAI (o proveedor compatible). Obligatoria para las demos. */
    public static String getOpenAiApiKey() {
        String key = DOTENV.get("OPENAI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                "Falta OPENAI_API_KEY. En la raíz del repo copia .env.example a .env y configura tu clave.");
        }
        return key.trim();
    }

    /** URL base opcional para APIs compatibles con OpenAI (ej. Azure, proxy). */
    public static String getOpenAiApiBase() {
        String base = DOTENV.get("OPENAI_API_BASE");
        return (base != null && !base.isBlank()) ? base.trim() : "https://api.openai.com/v1";
    }

    /** Obtiene cualquier variable; devuelve null si no existe. */
    public static String get(String name) {
        return DOTENV.get(name);
    }

    /** Obtiene variable con valor por defecto. */
    public static String get(String name, String defaultValue) {
        String v = DOTENV.get(name);
        return (v != null && !v.isBlank()) ? v.trim() : defaultValue;
    }
}
