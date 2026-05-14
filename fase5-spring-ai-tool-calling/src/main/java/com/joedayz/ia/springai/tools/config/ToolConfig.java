package com.joedayz.ia.springai.tools.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joedayz.ia.springai.tools.function.ClimaRequest;
import com.joedayz.ia.springai.tools.function.ClimaResponse;
import com.joedayz.ia.springai.tools.function.PaisRequest;
import com.joedayz.ia.springai.tools.function.PaisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * Configuración de Tools (Function Callbacks) para Spring AI.
 *
 * Lab 13: Cada @Bean que retorna Function<I,O> con @Description se registra
 * automáticamente como una herramienta disponible para el LLM.
 *
 * Flujo de Tool Calling:
 * 1. El usuario envía una pregunta
 * 2. El LLM analiza las herramientas disponibles
 * 3. El LLM decide llamar a una herramienta (o responder directamente)
 * 4. Spring AI ejecuta la función automáticamenteß
 * 5. El LLM incorpora el resultado y genera la respuesta final
 */
@Configuration
public class ToolConfig {

    private static final Logger log = LoggerFactory.getLogger(ToolConfig.class);
    private static final String REST_COUNTRIES_FIELDS = "?fields=name,capital,population,region,languages";

    private static final Map<String, String> COUNTRY_ALIASES = Map.ofEntries(
            Map.entry("alemania", "germany"),
            Map.entry("japon", "japan"),
            Map.entry("espana", "spain"),
            Map.entry("españa", "spain"),
            Map.entry("reino unido", "united kingdom"),
            Map.entry("estados unidos", "united states"),
            Map.entry("corea del sur", "south korea"),
            Map.entry("corea del norte", "north korea"),
            Map.entry("paises bajos", "netherlands"),
            Map.entry("países bajos", "netherlands")
    );

    /**
     * Lab 13: Tool obtenerClima() registrado como @Bean.
     * Spring AI detecta automáticamente este bean como una herramienta disponible.
     * El nombre del bean ("obtenerClima") se usa como nombre de la función.
     */
    @Bean("obtenerClima")
    @Description("Obtiene el clima actual para una ciudad dada. Retorna temperatura, condición climática y humedad.")
    public Function<ClimaRequest, ClimaResponse> obtenerClima() {
        Map<String, ClimaResponse> climaSimulado = Map.ofEntries(
                Map.entry("lima", new ClimaResponse("Lima", "22°C", "Parcialmente nublado", "78%")),
                Map.entry("madrid", new ClimaResponse("Madrid", "28°C", "Soleado", "35%")),
                Map.entry("bogotá", new ClimaResponse("Bogotá", "15°C", "Nublado", "82%")),
                Map.entry("bogota", new ClimaResponse("Bogotá", "15°C", "Nublado", "82%")),
                Map.entry("buenos aires", new ClimaResponse("Buenos Aires", "18°C", "Lluvioso", "90%")),
                Map.entry("ciudad de méxico", new ClimaResponse("Ciudad de México", "20°C", "Parcialmente nublado", "65%")),
                Map.entry("mexico city", new ClimaResponse("Ciudad de México", "20°C", "Parcialmente nublado", "65%")),
                Map.entry("new york", new ClimaResponse("New York", "25°C", "Soleado", "50%")),
                Map.entry("london", new ClimaResponse("London", "16°C", "Lluvioso", "85%")),
                Map.entry("londres", new ClimaResponse("Londres", "16°C", "Lluvioso", "85%")),
                Map.entry("tokyo", new ClimaResponse("Tokyo", "30°C", "Húmedo y caluroso", "75%")),
                Map.entry("santiago", new ClimaResponse("Santiago", "12°C", "Fresco y despejado", "45%")),
                Map.entry("quito", new ClimaResponse("Quito", "18°C", "Templado", "60%"))
        );

        return request -> {
            log.info("🌤️ Tool obtenerClima invocado para ciudad: {}", request.ciudad());
            String ciudadKey = request.ciudad().toLowerCase().trim();
            ClimaResponse response = climaSimulado.getOrDefault(ciudadKey,
                    new ClimaResponse(request.ciudad(), "20°C", "Datos no disponibles para esta ciudad", "N/A"));
            log.info("🌤️ Respuesta clima: {}", response);
            return response;
        };
    }

    /**
     * Reto: Tool que consulta una API REST real.
     * Usa restcountries.com para obtener información de países.
     * No requiere API key - es completamente gratuita.
     */
    @Bean("consultarPais")
    @Description("Consulta información real de un país usando una API REST externa. Retorna nombre oficial, capital, población, región e idiomas del país.")
    public Function<PaisRequest, PaisResponse> consultarPais() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        return request -> {
            log.info("🌍 Tool consultarPais invocado para país: {}", request.pais());
            try {
                String paisLimpio = sanitizeCountry(request.pais());
                JsonNode country = findCountryNode(restTemplate, mapper, paisLimpio);

                if (country == null || !country.isObject()) {
                    log.warn("🌍 País no encontrado en restcountries para: {}", request.pais());
                    return new PaisResponse(request.pais(), "No disponible", "No disponible", 0,
                            "No encontrado");
                }

                JsonNode nameNode = country.path("name");
                String nombre = nameNode.path("official").asText(nameNode.path("common").asText("No disponible"));
                String capital = country.path("capital").isArray() && country.path("capital").size() > 0
                        ? country.path("capital").get(0).asText("No disponible")
                        : "No disponible";
                long poblacion = country.path("population").asLong(0L);
                String region = country.path("region").asText("No disponible");

                StringBuilder idiomas = new StringBuilder();
                JsonNode languagesNode = country.path("languages");
                if (languagesNode.isObject()) {
                    languagesNode.fields().forEachRemaining(entry -> {
                        if (!idiomas.isEmpty()) {
                            idiomas.append(", ");
                        }
                        idiomas.append(entry.getValue().asText());
                    });
                }

                String idiomasValue = idiomas.isEmpty() ? "No disponible" : idiomas.toString();
                PaisResponse response = new PaisResponse(nombre, capital, region, poblacion, idiomasValue);
                log.info("🌍 Respuesta país: {}", response);
                return response;
            } catch (Exception e) {
                log.error("❌ Error consultando país: {}", e.getMessage());
                return new PaisResponse(request.pais(), "No disponible", "No disponible", 0,
                        "Error: " + e.getMessage());
            }
        };
    }

    private JsonNode findCountryNode(RestTemplate restTemplate, ObjectMapper mapper, String paisLimpio) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        candidates.add(paisLimpio);

        String normalized = removeDiacritics(paisLimpio.toLowerCase(Locale.ROOT));
        String alias = COUNTRY_ALIASES.get(normalized);
        if (alias != null) {
            candidates.add(alias);
        }
        if (!normalized.equalsIgnoreCase(paisLimpio)) {
            candidates.add(normalized);
        }

        List<String> endpoints = List.of("name", "translation");
        for (String candidate : new ArrayList<>(candidates)) {
            String encoded = URLEncoder.encode(candidate, StandardCharsets.UTF_8);
            for (String endpoint : endpoints) {
                String url = "https://restcountries.com/v3.1/" + endpoint + "/" + encoded + REST_COUNTRIES_FIELDS;
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                    JsonNode country = extractFirstCountry(mapper, response.getBody());
                    if (country != null) {
                        log.info("🌍 País encontrado usando {}='{}'", endpoint, candidate);
                        return country;
                    }
                } catch (HttpClientErrorException.NotFound notFound) {
                    log.debug("🌍 Sin resultados para {}='{}'", endpoint, candidate);
                } catch (Exception ex) {
                    log.warn("⚠️ Error consultando {}='{}': {}", endpoint, candidate, ex.getMessage());
                }
            }
        }
        return null;
    }

    private JsonNode extractFirstCountry(ObjectMapper mapper, String body) throws Exception {
        if (body == null || body.isBlank()) {
            return null;
        }
        JsonNode root = mapper.readTree(body);
        if (root.isArray() && !root.isEmpty()) {
            return root.get(0);
        }
        return null;
    }

    private String sanitizeCountry(String pais) {
        if (pais == null || pais.isBlank()) {
            return "";
        }
        return pais.trim().replaceAll("^[\\p{Punct}\\s]+|[\\p{Punct}\\s]+$", "");
    }

    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
