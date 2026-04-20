package com.example.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import io.quarkiverse.langchain4j.guardrails.OutputGuardrail;
import io.quarkiverse.langchain4j.guardrails.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JsonGuardrail implements OutputGuardrail {

    @Inject
    ObjectMapper mapper;

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String text = responseFromLLM == null ? null : responseFromLLM.text();
        if (text == null || text.isBlank()) {
            return reprompt("Empty output", null,
                    "Return only a JSON object like {\"elements\":[...]} with non-empty content.");
        }

        try {
            var root = mapper.readTree(text);
            if (!root.isObject()) {
                return reprompt("Invalid top-level JSON", null,
                        "Return a JSON object with one key named 'elements'.");
            }
            if (!root.has("elements") || !root.get("elements").isArray()) {
                return reprompt("Missing elements array", null,
                        "Return {\"elements\":[...]} and include at least one UI element.");
            }
        } catch (Exception e) {
            return reprompt("Invalid JSON", e, "Make sure you return a valid JSON object");
        }
        return success();
    }

}
