package com.sohamkamani.spring_rag_demo.rag;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DocumentLoader implements CommandLineRunner {

    private final VectorStore vectorStore;
    private final Resource sourcePdf;

    public DocumentLoader(
            VectorStore vectorStore,
            @Value("file:${rag.source.document.path}") Resource sourcePdf) {
        this.vectorStore = vectorStore;
        this.sourcePdf = sourcePdf;
    }

    @Override
    public void run(String... args) {
        if (!sourcePdf.exists()) {
            throw new IllegalStateException("Source PDF was not found at: " + sourcePdf);
        }

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(sourcePdf);
        List<Document> splitDocuments = new TokenTextSplitter().apply(pdfReader.get());
        vectorStore.add(splitDocuments);
        System.out.println("Loaded " + splitDocuments.size() + " chunks from PDF into VectorStore.");
    }
}
