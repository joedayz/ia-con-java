package com.example.gamerulesloader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class GameRulesLoaderApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameRulesLoaderApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(GameRulesLoaderApplication.class, args);
  }

  @Bean
  ApplicationRunner go(FunctionCatalog catalog) {
    Runnable composedFunction = catalog.lookup(null);
    return args -> {
      composedFunction.run();
    };
  }

  
  @Bean
  Function<Flux<Message<byte[]>>, Flux<Document>> documentReader() {
    return resourceFlux -> resourceFlux
        .map(message -> {
          var fileName = (String) message.getHeaders().get("file_name");   
          LOGGER.info("Reading document from file: {}", fileName);

          var fileBytes = message.getPayload();
          var document = new TikaDocumentReader(
              new ByteArrayResource(fileBytes))
              .get()
              .getFirst();
          if (fileName != null && !fileName.isBlank()) {
            document.getMetadata().put("sourceFileName", fileName);
          }
          if (isPremiumDocument(fileName)) {
            document.getMetadata().put("documentType", "PREMIUM"); 
          }
          return document;
        })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private boolean isPremiumDocument(String fileName) {    
    var baseFilename = fileName
        .substring(0, fileName.toString().lastIndexOf('.'));
    return baseFilename.endsWith("-premium");
  }
  

  @Bean
  Function<Flux<Document>, Flux<List<Document>>> splitter() {
    return documentFlux ->
        documentFlux
            .map(incoming ->
                new TokenTextSplitter().apply(List.of(incoming))).subscribeOn(Schedulers.boundedElastic());
  }

  @Value("classpath:/promptTemplates/nameOfTheGame.st")
  Resource nameOfTheGameTemplateResource;

  private static final Pattern EXTENSION = Pattern.compile("\\.[^.]+$");

  @Bean
  Function<Flux<List<Document>>, Flux<List<Document>>>
              titleDeterminer(ChatClient.Builder chatClientBuilder) {

    var chatClient = chatClientBuilder.build();

    return documentListFlux -> documentListFlux
        .map(documents -> {
          if (!documents.isEmpty()) {
            var firstDocument = documents.getFirst(); 

            var gameTitle = chatClient.prompt()
                .user(userSpec -> userSpec
                    .text(nameOfTheGameTemplateResource)
                    .param("document", firstDocument.getText())) 
                .call()
                .entity(GameTitle.class);

            var title = (gameTitle == null) ? null : gameTitle.title();
            if (title == null || title.isBlank() || title.equals("UNKNOWN")) {
              var filenameTitle = titleFromFilename(firstDocument);
              if (filenameTitle == null) {
                LOGGER.warn("Unable to determine the name of a game; not adding to vector store.");
                documents = Collections.emptyList();
                return documents;
              }
              gameTitle = filenameTitle;
              LOGGER.info("Using filename-derived title: {}", gameTitle.title());
            }

            LOGGER.info("Determined game title to be {}", gameTitle.title());
            var normalizedTitle = gameTitle.getNormalizedTitle();
            documents = documents.stream().peek(document -> {
              document.getMetadata().put("gameTitle", normalizedTitle);
            }).toList();
          }

          return documents;
        });
  }

  private GameTitle titleFromFilename(Document document) {
    var fileName = (String) document.getMetadata().get("sourceFileName");
    if (fileName == null || fileName.isBlank()) return null;

    var base = EXTENSION.matcher(fileName).replaceAll("");
    if (base.endsWith("-premium")) base = base.substring(0, base.length() - "-premium".length());

    // turn Spec-Driven-Development -> "Spec Driven Development"
    var words = base.replace('_', ' ').replace('-', ' ').trim().replaceAll("\\s+", " ");
    if (words.isBlank()) return null;

    var titleCased = Arrays.stream(words.split(" "))
        .filter(w -> !w.isBlank())
        .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase())
        .reduce((a, b) -> a + " " + b)
        .orElse("");
    if (titleCased.isBlank()) return null;
    return new GameTitle(titleCased);
  }

  @Bean
  Consumer<Flux<List<Document>>> vectorStoreConsumer(VectorStore vectorStore) {
    return documentFlux -> documentFlux
      .filter(documents -> documents != null && !documents.isEmpty())
      .doOnNext(documents -> {
        var docCount = documents.size();
        LOGGER.info("Writing {} documents to vector store.", docCount);
        vectorStore.accept(documents);
        LOGGER.info("{} documents have been written to vector store.", docCount);
      })
      .subscribe();
  }

}
