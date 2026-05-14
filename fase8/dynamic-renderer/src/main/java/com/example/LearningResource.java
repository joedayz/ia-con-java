package com.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

import com.example.model.ListElement;
import com.example.model.RenderedResponse;
import com.example.model.TextElement;
import com.example.model.UIElement;
import com.example.model.WebsiteElement;
import com.example.service.LearningAssistant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class LearningResource {

    private static final Pattern BULLET_PATTERN = Pattern.compile("^(?:[-*•]+|\\d+[.)])\\s+(.*)$");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

    @Inject
    LearningAssistant.Researcher researcherAI;

    @Inject
    LearningAssistant.Renderer rendererAI;

    @Inject
    ObjectMapper objectMapper;

    private static final Logger LOG = Logger.getLogger(LearningResource.class);
    private static final int MAX_RENDERER_INPUT_CHARS = 4000;

    @POST
    @Path("/ask")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String ask(@FormParam("question") String question) throws Exception {
        // 1. Call the Researcher AI to get the content
        String researchResult = researcherAI.research(question);

        // Trim researcher output to reduce renderer input tokens
        if (researchResult.length() > MAX_RENDERER_INPUT_CHARS) {
            researchResult = researchResult.substring(0, MAX_RENDERER_INPUT_CHARS);
        }

        // 2. Call the Renderer AI to structure the content
        String jsonResponse = rendererAI.render(researchResult);
        LOG.infof("RAW JSON from Renderer (length=%d)", jsonResponse == null ? 0 : jsonResponse.length());

        if (jsonResponse == null || jsonResponse.isBlank()) {
            LOG.error("Renderer returned empty content. Check renderer model config and output constraints.");
            return "{\"elements\":[]}";
        }

        String cleanJson = sanitizeAndExtractJson(jsonResponse);

        // 3. Parse the JSON into our POJOs
        RenderedResponse renderedResponse;
        try {
            renderedResponse = objectMapper.readValue(cleanJson, RenderedResponse.class);
            renderedResponse = enrichRenderedResponse(researchResult, renderedResponse);
            if (renderedResponse != null && renderedResponse.elements != null) {
                for (UIElement element : renderedResponse.elements) {
                    LOG.infof("Element class: %s, renderHint: %s",
                            element == null ? "null" : element.getClass().getName(),
                            element == null ? "null" : element.renderHint);
                }
            }

        } catch (JsonProcessingException e) {
            LOG.errorf("Failed to parse JSON: %s", cleanJson, e);
            return cleanJson;
        }

        return objectMapper.writeValueAsString(renderedResponse);
    }

    private String sanitizeAndExtractJson(String raw) {
        String trimmed = raw.trim();

        if (trimmed.startsWith("```") && trimmed.endsWith("```")) {
            trimmed = trimmed.replaceFirst("^```(?:json)?\\s*", "");
            trimmed = trimmed.replaceFirst("\\s*```$", "");
            trimmed = trimmed.trim();
        }

        if (trimmed.startsWith("{")) {
            return trimmed;
        }

        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            String candidate = trimmed.substring(start, end + 1);
            try {
                JsonNode node = objectMapper.readTree(candidate);
                if (node.isObject()) {
                    return candidate;
                }
            } catch (JsonProcessingException ignored) {
                // fall through and return original text
            }
        }

        return trimmed;
    }

    RenderedResponse enrichRenderedResponse(String researchResult, RenderedResponse renderedResponse) {
        RenderedResponse fallbackResponse = extractFallbackResponse(researchResult);
        if (fallbackResponse.elements.isEmpty()) {
            return renderedResponse;
        }

        if (renderedResponse == null || renderedResponse.elements == null || renderedResponse.elements.isEmpty()) {
            return fallbackResponse;
        }

        Map<String, UIElement> mergedByTitle = new LinkedHashMap<>();
        for (UIElement element : renderedResponse.elements) {
            if (element == null) {
                continue;
            }
            mergedByTitle.putIfAbsent(elementKey(element), element);
        }

        boolean incompleteResponse = renderedResponse.elements.size() <= 1
                || !containsSection(renderedResponse.elements, "articles")
                || !containsSection(renderedResponse.elements, "recommended resources");

        if (!incompleteResponse) {
            return renderedResponse;
        }

        for (UIElement element : fallbackResponse.elements) {
            mergedByTitle.putIfAbsent(elementKey(element), element);
        }

        renderedResponse.elements = new ArrayList<>(mergedByTitle.values());
        return renderedResponse;
    }

    RenderedResponse extractFallbackResponse(String researchResult) {
        RenderedResponse response = new RenderedResponse();
        response.elements = new ArrayList<>();

        if (researchResult == null || researchResult.isBlank()) {
            return response;
        }

        List<String> summaryLines = new ArrayList<>();
        Map<String, List<String>> sections = new LinkedHashMap<>();
        String currentSection = null;

        for (String rawLine : researchResult.replace("\r", "").split("\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            String detectedSection = detectSectionTitle(line);
            if (detectedSection != null) {
                currentSection = detectedSection;
                sections.computeIfAbsent(currentSection, ignored -> new ArrayList<>());
                continue;
            }

            Matcher bulletMatcher = BULLET_PATTERN.matcher(line);
            String content = bulletMatcher.matches() ? bulletMatcher.group(1).trim() : line;

            if (currentSection != null) {
                sections.computeIfAbsent(currentSection, ignored -> new ArrayList<>()).add(content);
            } else if (summaryLines.size() < 4) {
                summaryLines.add(content);
            }
        }

        if (!summaryLines.isEmpty()) {
            response.elements.add(textElement("Overview", String.join(" ", summaryLines)));
        }

        for (Map.Entry<String, List<String>> entry : sections.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            response.elements.add(listElement(entry.getKey(), entry.getValue()));
        }

        if (response.elements.isEmpty()) {
            List<String> urls = new ArrayList<>();
            Matcher matcher = URL_PATTERN.matcher(researchResult);
            while (matcher.find()) {
                urls.add(matcher.group());
            }
            if (!urls.isEmpty()) {
                response.elements.add(websiteElement("Source", urls.get(0)));
            }
        }

        return response;
    }

    private boolean containsSection(List<UIElement> elements, String title) {
        for (UIElement element : elements) {
            String elementTitle = extractTitle(element);
            if (elementTitle != null && elementTitle.toLowerCase(Locale.ROOT).contains(title)) {
                return true;
            }
        }
        return false;
    }

    private String elementKey(UIElement element) {
        String title = extractTitle(element);
        if (title == null || title.isBlank()) {
            return element.renderHint + ":" + element.hashCode();
        }
        return element.renderHint + ":" + title.trim().toLowerCase(Locale.ROOT);
    }

    private String extractTitle(UIElement element) {
        if (element instanceof TextElement textElement && textElement.data != null) {
            return textElement.data.title;
        }
        if (element instanceof ListElement listElement && listElement.data != null) {
            return listElement.data.title;
        }
        if (element instanceof WebsiteElement websiteElement && websiteElement.data != null) {
            return websiteElement.data.title;
        }
        return null;
    }

    private String detectSectionTitle(String line) {
        String normalized = line.replace("#", "").replace("*", "").trim();
        String lowercase = normalized.toLowerCase(Locale.ROOT);

        if (lowercase.startsWith("summary") || lowercase.startsWith("overview")) {
            return "Overview";
        }
        if (lowercase.startsWith("books")) {
            return "Books";
        }
        if (lowercase.startsWith("podcasts")) {
            return "Podcasts";
        }
        if (lowercase.startsWith("articles")) {
            return "Articles";
        }
        if (lowercase.startsWith("recommended resources") || lowercase.startsWith("resources")) {
            return "Recommended Resources";
        }
        if (lowercase.startsWith("websites") || lowercase.startsWith("links")) {
            return "Websites";
        }
        return null;
    }

    private TextElement textElement(String title, String text) {
        TextElement element = new TextElement();
        element.renderHint = "text";
        element.data = new TextElement.Data();
        element.data.title = title;
        element.data.text = text;
        return element;
    }

    private ListElement listElement(String title, List<String> items) {
        ListElement element = new ListElement();
        element.renderHint = "list";
        element.data = new ListElement.Data();
        element.data.title = title;
        element.data.items = new ArrayList<>(items);
        return element;
    }

    private WebsiteElement websiteElement(String title, String url) {
        WebsiteElement element = new WebsiteElement();
        element.renderHint = "website";
        element.data = new WebsiteElement.Data();
        element.data.title = title;
        element.data.url = url;
        return element;
    }

}