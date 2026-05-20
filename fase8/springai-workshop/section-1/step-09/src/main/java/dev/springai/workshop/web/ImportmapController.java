package dev.springai.workshop.web;

import jakarta.annotation.PostConstruct;
import org.mvnpm.importmap.Aggregator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Builds and serves the dynamic import map for mvnpm dependencies (wc-chatbot, Vaadin, lit).
 */
@RestController
public class ImportmapController {

    private String importmap;

    @GetMapping(value = "/_importmap/dynamic.importmap", produces = "application/importmap+json")
    public String importMap() {
        return importmap;
    }

    @GetMapping(value = "/_importmap/dynamic-importmap.js", produces = "application/javascript")
    public String importMapJs() {
        return JAVASCRIPT_CODE.formatted(importmap);
    }

    @PostConstruct
    void init() {
        Aggregator aggregator = new Aggregator();
        aggregator.addMapping("icons/", "/icons/");
        aggregator.addMapping("components/", "/components/");
        aggregator.addMapping("fonts/", "/fonts/");
        importmap = aggregator.aggregateAsJson();
    }

    private static final String JAVASCRIPT_CODE = """
            const im = document.createElement('script');
            im.type = 'importmap';
            im.textContent = JSON.stringify(%s);
            document.currentScript.after(im);
            """;
}
