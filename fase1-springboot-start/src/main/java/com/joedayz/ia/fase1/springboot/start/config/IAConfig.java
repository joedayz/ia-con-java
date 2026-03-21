package com.joedayz.ia.fase1.springboot.start.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración multi-provider para OpenAI y Anthropic
 */
@Configuration
@ConfigurationProperties(prefix = "ia")
public class IAConfig {
    
    private OpenAI openai = new OpenAI();
    private Anthropic anthropic = new Anthropic();

    public OpenAI getOpenai() {
        return openai;
    }

    public void setOpenai(OpenAI openai) {
        this.openai = openai;
    }

    public Anthropic getAnthropic() {
        return anthropic;
    }

    public void setAnthropic(Anthropic anthropic) {
        this.anthropic = anthropic;
    }

    public static class OpenAI {
        private String key;
        private String base;
        private String model;
        private Integer maxTokens;
        private String timeout;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

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

    public static class Anthropic {
        private String key;
        private String base;
        private String model;
        private Integer maxTokens;
        private String timeout;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

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
