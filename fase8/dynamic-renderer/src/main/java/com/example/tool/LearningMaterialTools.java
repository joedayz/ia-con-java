package com.example.tool;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.jsoup.Jsoup;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LearningMaterialTools {

    private static final Logger LOG = Logger.getLogger(LearningMaterialTools.class);

    @Tool("Searches the web using DuckDuckGo. Pass a plain text search query as input, for example: 'Java programming tutorials'.")
    String webSearch(@P("A plain text search query, e.g. 'Universidad Tecnológica del Perú'") String query) throws IOException {
        String webUrl = "https://html.duckduckgo.com/html/?q=" + query;
        String text = Jsoup.connect(webUrl).get().getElementsByClass("results").text().substring(0, 2000);
        LOG.infof("Parsed search response: %s", text);
        return text;
    }
}