package com.joedayz.ia.langchain4j.tools.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Reto: Herramienta que consulta una API REST real.
 *
 * Usa restcountries.com (gratuita, sin API key) para obtener
 * información real de cualquier país del mundo.
 *
 * Demuestra el uso de @Tool con @P (parámetro con descripción)
 * y llamadas HTTP reales dentro de una herramienta.
 */
@Component
public class PaisApiTools {

    private static final Logger log = LoggerFactory.getLogger(PaisApiTools.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Tool("Consulta información real de un país usando la API REST de restcountries.com. "
            + "Retorna nombre, capital, población, región e idiomas del país.")
    public String consultarPais(@P("Nombre del país a consultar, por ejemplo: Perú, Colombia, Japan") String pais) {
        log.info("🌍 Tool consultarPais invocado para: {}", pais);
        try {
            String encoded = URLEncoder.encode(pais, StandardCharsets.UTF_8);
            String url = "https://restcountries.com/v3.1/name/" + encoded
                    + "?fields=name,capital,population,region,languages";
            String json = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(json);
            JsonNode country = root.get(0);

            String nombre = country.path("name").path("common").asText();
            String capital = country.path("capital").has(0)
                    ? country.path("capital").get(0).asText() : "No disponible";
            long poblacion = country.path("population").asLong();
            String region = country.path("region").asText();

            StringBuilder idiomas = new StringBuilder();
            country.path("languages").fields().forEachRemaining(entry -> {
                if (!idiomas.isEmpty()) idiomas.append(", ");
                idiomas.append(entry.getValue().asText());
            });

            String result = String.format(
                    "País: %s, Capital: %s, Población: %,d, Región: %s, Idiomas: %s",
                    nombre, capital, poblacion, region, idiomas);
            log.info("🌍 Resultado: {}", result);
            return result;
        } catch (Exception e) {
            log.error("❌ Error consultando país '{}': {}", pais, e.getMessage());
            return "Error al consultar información del país '" + pais + "': " + e.getMessage();
        }
    }
}
