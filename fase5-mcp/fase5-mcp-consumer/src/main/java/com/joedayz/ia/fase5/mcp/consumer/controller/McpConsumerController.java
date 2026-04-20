package com.joedayz.ia.fase5.mcp.consumer.controller;

import com.joedayz.ia.fase5.mcp.consumer.dto.ChatRequest;
import com.joedayz.ia.fase5.mcp.consumer.dto.ChatResponse;
import com.joedayz.ia.fase5.mcp.consumer.dto.PromptRequest;
import com.joedayz.ia.fase5.mcp.consumer.service.McpGatewayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcp")
public class McpConsumerController {

    private final McpGatewayService gatewayService;

    public McpConsumerController(McpGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return new ChatResponse(gatewayService.ask(request.message()));
    }

    @GetMapping("/demo/cronograma/{clase}")
    public ChatResponse cronograma(@PathVariable String clase) {
        String prompt = "Consulta la clase " + clase + " del cronograma usando la herramienta MCP disponible.";
        return new ChatResponse(gatewayService.ask(prompt));
    }

    @GetMapping("/demo/modulo/{fase}")
    public ChatResponse modulo(@PathVariable String fase) {
        String prompt = "Resume el modulo " + fase + " usando la herramienta MCP disponible.";
        return new ChatResponse(gatewayService.ask(prompt));
    }

    @PostMapping("/demo/actividad")
    public ChatResponse actividad(@RequestBody PromptRequest request) {
        String prompt = "Genera una actividad fase5 para tema " + request.tema() + " con nivel "
            + (request.nivel() == null || request.nivel().isBlank() ? "intermedio" : request.nivel())
            + " usando la herramienta MCP disponible.";
        return new ChatResponse(gatewayService.ask(prompt));
    }
}

