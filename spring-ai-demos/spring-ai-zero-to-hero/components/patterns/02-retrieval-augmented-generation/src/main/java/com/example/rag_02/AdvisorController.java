package com.example.rag_02;

import com.example.JsonReader2;
import com.example.data.DataFiles;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag/02")
public class AdvisorController {

  private static final Logger logger = LoggerFactory.getLogger(AdvisorController.class);

  private static final int INITIAL_TOKEN_CHUNK_SIZE = 120;
  private static final int MAX_CHARS_PER_CHUNK = 1200;
  private static final int MIN_CHARS_PER_CHUNK = 200;
  private static final int CHUNK_OVERLAP_CHARS = 80;

  private final DataFiles dataFiles;

  private final VectorStore vectorStore;

  private final ChatClient chatClient;

  public AdvisorController(
      VectorStore vectorStore, DataFiles dataFiles, ChatClient.Builder builder) {
    this.dataFiles = dataFiles;
    this.vectorStore = vectorStore;
    this.chatClient =
        builder
            .defaultSystem(
                """
				                You are a helpful assistant at an e-bike store. Your job is to answer
				                customer questions about e-bikes that we are selling. The questions should
				                be answered based on the provided bike specifications. If you don't know
				                the answer politely tell the customer you don't know the answer, then ask
				                the customer a followup question to try and clarify the question they are
				                asking.
				""")
            .build();
  }

  private static final String USER_TEXT_ADVISE =
      """

      Given the context information below , surrounded ---------------------, and provided
      history information and not prior knowledge, reply to the user comment.
      If the answer is not in the context, inform the user that you can't answer the question.

			---------------------
			{question_answer_context}
			---------------------
			""";

  @GetMapping("/load")
  public String load() throws IOException {
    // turn the json specs file into a document per bike
    DocumentReader reader =
        new JsonReader2(
            this.dataFiles.getBikesResource(), "name", "price", "shortDescription", "description");
    List<Document> documents = reader.get();

    // add the documents to the vector store
    TokenTextSplitter splitter =
        new TokenTextSplitter(INITIAL_TOKEN_CHUNK_SIZE, 50, 5, 10000, true);
    List<Document> splitDocs = splitter.split(documents);
    splitDocs.forEach(document -> addWithFallback(document, MAX_CHARS_PER_CHUNK));
    return "vector store loaded with %s documents (%s chunks)"
        .formatted(documents.size(), splitDocs.size());
  }

  private void addWithFallback(Document document, int maxChars) {
    for (Document candidate : splitByChars(document, maxChars)) {
      try {
        this.vectorStore.add(List.of(candidate));
      } catch (RuntimeException ex) {
        if (!isContextOverflow(ex)) {
          throw ex;
        }
        if (maxChars <= MIN_CHARS_PER_CHUNK) {
          logger.warn(
              "Skipping chunk after context overflow retries. metadata={}, charLength={}",
              candidate.getMetadata(),
              candidate.getText() == null ? 0 : candidate.getText().length());
          continue;
        }
        addWithFallback(candidate, Math.max(MIN_CHARS_PER_CHUNK, maxChars / 2));
      }
    }
  }

  private boolean isContextOverflow(RuntimeException ex) {
    String message = ex.getMessage();
    return message != null && message.contains("input length exceeds the context length");
  }

  private List<Document> splitByChars(Document document, int maxChars) {
    String text = document.getText();
    if (text == null || text.isBlank() || text.length() <= maxChars) {
      return List.of(document);
    }

    List<Document> result = new ArrayList<>();
    int start = 0;
    int chunkIndex = 0;

    while (start < text.length()) {
      int end = Math.min(start + maxChars, text.length());
      String chunk = text.substring(start, end);

      Map<String, Object> metadata =
          document.getMetadata() == null ? new HashMap<>() : new HashMap<>(document.getMetadata());
      metadata.put("chunk_index", chunkIndex++);
      metadata.put("chunk_start", start);
      metadata.put("chunk_end", end);

      result.add(new Document(chunk, metadata));

      if (end == text.length()) {
        break;
      }
      start = Math.max(0, end - CHUNK_OVERLAP_CHARS);
    }
    return result;
  }

  @GetMapping("query")
  public String query(
      @RequestParam(value = "topic", defaultValue = "Which bikes have extra long range? /n")
          String topic) {

    return this.chatClient
        .prompt()
        .advisors(
            QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(new PromptTemplate(USER_TEXT_ADVISE))
                .build())
        .user(topic)
        .call()
        .content();
  }
}
