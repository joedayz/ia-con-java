package dev.springai.workshop.pricing.remote.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Metadatos del agente remoto (equiv. {@code AgentCard} en A2A).
 */
@RestController
public class AgentCardController {

    @GetMapping("/.well-known/agent.json")
    public Map<String, Object> agentCard() {
        return Map.of(
                "name", "Pricing Agent",
                "description", "Estimates the market value of a vehicle based on make, model, year, and condition.",
                "url", "http://localhost:8888/",
                "version", "1.0.0",
                "protocolVersion", "1.0.0",
                "preferredTransport", "HTTP+JSON",
                "skills", List.of(Map.of(
                        "id", "pricing",
                        "name", "Vehicle pricing",
                        "description", "Estimates market value",
                        "tags", List.of("pricing", "valuation"))),
                "endpoints", Map.of(
                        "estimate", "POST /api/pricing/estimate"));
    }
}
