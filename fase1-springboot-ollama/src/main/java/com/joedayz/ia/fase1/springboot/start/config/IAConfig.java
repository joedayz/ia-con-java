package com.joedayz.ia.fase1.springboot.start.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para Ollama
 */
@Configuration
@ConfigurationProperties(prefix = "ia")
public class IAConfig {
    
    private Ollama ollama = new Ollama();

    public Ollama getOllama() {
        return ollama;
    }

    public void setOllama(Ollama ollama) {
        this.ollama = ollama;
    }

    public static class Ollama {
        private String base;
        private String model;
        private Integer maxTokens;
        private String timeout;


        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }

        public String getTimeout() {
            return timeout;
        }

        public void setTimeout(String timeout) {
            this.timeout = timeout;
        }
    }
}
