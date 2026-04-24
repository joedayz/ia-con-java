package com.example.boardgamebuddy;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

  @Bean
  @ConditionalOnProperty(name = "otlp.tracing.enabled", havingValue = "true")
  public OtlpGrpcSpanExporter otlpHttpSpanExporter(
        @Value("${otlp.tracing.url}") String url) {
    return OtlpGrpcSpanExporter.builder().setEndpoint(url).build();
  }

}
