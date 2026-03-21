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
        String key = get("OPENAI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                "Falta OPENAI_API_KEY. Configúrala como variable de entorno o en .env");
        }
        return key.trim();
    }

    /** URL base opcional para APIs compatibles con OpenAI (ej. Azure, proxy). */
    public static String getOpenAiApiBase() {
        String base = get("OPENAI_API_BASE");
        return (base != null && !base.isBlank()) ? base.trim() : "https://api.openai.com/v1";
    }

    /** 
     * Obtiene cualquier variable; devuelve null si no existe.
     * Prioridad: 1) System.getenv(), 2) archivo .env 
     */
    public static String get(String name) {
        // Primero intenta System.getenv (variables exportadas con source ~/.api-keys)
        String systemValue = System.getenv(name);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        // Si no existe en system, busca en archivo .env
        return DOTENV.get(name);
    }

    /** Obtiene variable con valor por defecto. */
    public static String get(String name, String defaultValue) {
        String v = get(name);
        return (v != null && !v.isBlank()) ? v.trim() : defaultValue;
    }

    /** API Key de Anthropic (Claude). Opcional. */
    public static String getAnthropicApiKey() {
        String key = get("ANTHROPIC_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                "Falta ANTHROPIC_API_KEY. Configúrala como variable de entorno o en .env");
        }
        return key.trim();
    }

    /** URL base de Anthropic API. */
    public static String getAnthropicApiBase() {
        String base = get("ANTHROPIC_API_BASE");
        return (base != null && !base.isBlank()) ? base.trim() : "https://api.anthropic.com/v1";
    }

    /** Verifica si una API key específica está disponible. */
    public static boolean hasKey(String keyName) {
        String value = get(keyName);
        return value != null && !value.isBlank();
    }
}
