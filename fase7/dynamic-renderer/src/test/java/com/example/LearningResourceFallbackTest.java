package com.example;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.model.ListElement;
import com.example.model.RenderedResponse;
import com.example.model.TextElement;

class LearningResourceFallbackTest {

    @Test
    void extractFallbackResponsePreservesArticlesAndRecommendedResources() {
        LearningResource resource = new LearningResource();

        String researchResult = """
                Markus Eisele is a prominent figure in the enterprise Java world.
                He works at Red Hat and frequently shares practical developer guidance.

                Articles
                - Improve Developer Experience and Productivity with Red Hat

                Recommended Resources
                - Books
                - Podcasts
                - Articles
                - Websites
                """;

        RenderedResponse response = resource.extractFallbackResponse(researchResult);

        assertEquals(3, response.elements.size());
        assertTrue(response.elements.get(0) instanceof TextElement);
        assertTrue(response.elements.get(1) instanceof ListElement);
        assertTrue(response.elements.get(2) instanceof ListElement);

        ListElement articles = (ListElement) response.elements.get(1);
        ListElement recommendedResources = (ListElement) response.elements.get(2);

        assertEquals("Articles", articles.data.title);
        assertEquals(List.of("Improve Developer Experience and Productivity with Red Hat"), articles.data.items);
        assertEquals("Recommended Resources", recommendedResources.data.title);
        assertEquals(List.of("Books", "Podcasts", "Articles", "Websites"), recommendedResources.data.items);
    }
}