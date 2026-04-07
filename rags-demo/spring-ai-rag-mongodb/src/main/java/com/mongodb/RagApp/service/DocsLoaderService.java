package com.mongodb.RagApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DocsLoaderService {

    private static final int MAX_TOKENS_PER_CHUNK = 2000; // Adjust this value as needed
    private static final int BATCH_SIZE = 100;
    private static final int MAX_SKIPPED_DETAILS = 10;

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final String docsPath;

    @Autowired
    public DocsLoaderService(VectorStore vectorStore,
                             ObjectMapper objectMapper,
                             @Value("${app.docs.path:docs}") String docsPath) {
        this.vectorStore = vectorStore;
        this.objectMapper = objectMapper;
        this.docsPath = docsPath;
    }

    public String loadDocs() {
        Path root = Paths.get(this.docsPath).toAbsolutePath().normalize();
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            return "No se encontro la carpeta de documentos: " + root;
        }

        try {
            List<Path> files = Files.walk(root)
                    .filter(Files::isRegularFile)
                    .toList();

            if (files.isEmpty()) {
                return "La carpeta docs esta vacia: " + root;
            }

            List<Document> documents = new ArrayList<>();
            int processedFiles = 0;
            int chunkCount = 0;
            int skippedFiles = 0;
            List<String> skippedReasons = new ArrayList<>();

            for (Path file : files) {
                try {
                    if (isPdfFile(file)) {
                        List<Document> pdfChunks = readPdfDocuments(file);
                        if (pdfChunks.isEmpty()) {
                            skippedFiles++;
                            addSkipReason(skippedReasons, file, "pdf sin contenido");
                            continue;
                        }

                        processedFiles++;
                        documents.addAll(pdfChunks);
                        chunkCount += pdfChunks.size();

                        if (documents.size() >= BATCH_SIZE) {
                            vectorStore.add(documents);
                            documents.clear();
                        }
                        continue;
                    }

                    String content = readFileContent(file);
                    if (content.isBlank()) {
                        skippedFiles++;
                        addSkipReason(skippedReasons, file, "archivo vacio");
                        continue;
                    }

                    processedFiles++;
                    List<String> chunks = splitIntoChunks(content);
                    for (String chunk : chunks) {
                        documents.add(createDocument(file, chunk));
                        chunkCount++;
                    }

                    if (documents.size() >= BATCH_SIZE) {
                        vectorStore.add(documents);
                        documents.clear();
                    }
                } catch (Exception fileError) {
                    skippedFiles++;
                    addSkipReason(skippedReasons, file, fileError.getMessage());
                }
            }

            if (!documents.isEmpty()) {
                vectorStore.add(documents);
            }

            String summary = "Documentos cargados desde " + root
                    + " | archivos procesados: " + processedFiles
                    + " | chunks: " + chunkCount
                    + " | omitidos: " + skippedFiles;
            if (!skippedReasons.isEmpty()) {
                return summary + " | detalle omitidos: " + String.join("; ", skippedReasons);
            }
            return summary;
        } catch (Exception e) {
            return "An error occurred while adding documents: " + e.getMessage();
        }
    }

    private List<Document> readPdfDocuments(Path file) throws IOException {
        List<Document> pdfDocs = new ArrayList<>();
        try (PDDocument pdf = Loader.loadPDF(Files.readAllBytes(file))) {
            PDFTextStripper stripper = new PDFTextStripper();
            for (int page = 1; page <= pdf.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(pdf);
                if (pageText == null || pageText.isBlank()) {
                    continue;
                }

                List<String> chunks = splitIntoChunks(pageText);
                for (String chunk : chunks) {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("sourceName", file.getFileName().toString());
                    metadata.put("path", file.toAbsolutePath().toString());
                    metadata.put("page", page);
                    pdfDocs.add(new Document(chunk, metadata));
                }
            }
        }
        return pdfDocs;
    }

    private String readFileContent(Path file) throws IOException {
        if (!isSupportedFile(file)) {
            throw new IOException("extension no soportada");
        }

        String raw = readUtf8(file);

        if (file.toString().endsWith(".json")) {
            // Supports either JSON array/object or line-delimited JSON with a "body" field.
            if (raw.trim().isEmpty()) {
                return "";
            }

            if (raw.stripLeading().startsWith("{")) {
                Map<String, Object> json = objectMapper.readValue(raw, Map.class);
                Object body = json.get("body");
                if (body instanceof String bodyText && !bodyText.isBlank()) {
                    return bodyText;
                }
            }
            return raw;
        }

        return raw;
    }

    private String readUtf8(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(java.nio.ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            throw new IOException("archivo no UTF-8 o binario", e);
        }
    }

    private boolean isSupportedFile(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".txt")
                || name.endsWith(".md")
                || name.endsWith(".json")
                || name.endsWith(".csv")
                || name.endsWith(".log")
                || name.endsWith(".xml")
                || name.endsWith(".yaml")
                || name.endsWith(".yml");
    }

    private boolean isPdfFile(Path file) {
        return file.getFileName().toString().toLowerCase().endsWith(".pdf");
    }

    private void addSkipReason(List<String> skippedReasons, Path file, String reason) {
        if (skippedReasons.size() < MAX_SKIPPED_DETAILS) {
            skippedReasons.add(file.getFileName() + " (" + reason + ")");
        }
    }

    private Document createDocument(Path file, String content) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("sourceName", file.getFileName().toString());
        metadata.put("path", file.toAbsolutePath().toString());
        return new Document(content, metadata);
    }

    private List<String> splitIntoChunks(String content) {
        List<String> chunks = new ArrayList<>();
        String[] words = content.split("\\s+");
        StringBuilder chunk = new StringBuilder();
        int tokenCount = 0;

        for (String word : words) {
            // Estimate token count for the word (approximated by character length for simplicity)
            int wordTokens = word.length() / 4;  // Rough estimate: 1 token = ~4 characters

            if (tokenCount + wordTokens > DocsLoaderService.MAX_TOKENS_PER_CHUNK) {
                chunks.add(chunk.toString());
                chunk.setLength(0); // Clear the buffer
                tokenCount = 0;
            }

            chunk.append(word).append(" ");
            tokenCount += wordTokens;
        }

        if (!chunk.isEmpty()) {
            chunks.add(chunk.toString());
        }

        return chunks;
    }
}
