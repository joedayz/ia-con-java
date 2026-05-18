package dev.springai.workshop.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Herramientas MCP expuestas por el servidor (equivalente a {@code Weather.java} en Quarkus).
 */
@Service
public class WeatherTools {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.open-meteo.com")
            .build();

    @Tool(description = "Get weather forecast for a location.")
    public String getForecast(
            @ToolParam(description = "Latitude of the location") double latitude,
            @ToolParam(description = "Longitude of the location") double longitude) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("forecast_days", 16)
                        .queryParam("hourly", "temperature_2m,snowfall,rain,precipitation,precipitation_probability")
                        .build())
                .retrieve()
                .body(String.class);
    }
}
