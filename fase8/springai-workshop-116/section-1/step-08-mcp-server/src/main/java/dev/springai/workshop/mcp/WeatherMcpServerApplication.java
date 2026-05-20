package dev.springai.workshop.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherMcpServerApplication.class, args);
    }

    @Bean
    ToolCallbackProvider weatherToolCallbacks(WeatherTools weatherTools) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherTools).build();
    }
}
