package demo.elastic.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    // Both beans autowired from default configuration
    private ElasticsearchVectorStore vectorStore;
    private ChatClient chatClient;

    public RagService(ElasticsearchVectorStore vectorStore, ChatClient.Builder clientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = clientBuilder.build();
    }

    public void ingestPDF(String path) {
        Resource pdfResource = resolvePdfResource(path);

        // Spring AI utility class to read a PDF file page by page
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource);
        List<Document> docbatch = pdfReader.read();

        // Sending batch of documents to vector store
        // applying tokenizer
        docbatch = new TokenTextSplitter().apply(docbatch);
        vectorStore.doAdd(docbatch);
    }

    private Resource resolvePdfResource(String rawPath) {
        if (rawPath == null || rawPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Path del PDF vacio");
        }

        String normalizedPath = rawPath.trim();

        if (normalizedPath.startsWith("classpath:")) {
            String classpathLocation = normalizedPath.substring("classpath:".length());
            Resource classpathResource = new ClassPathResource(classpathLocation);
            if (!classpathResource.exists()) {
                throw new IllegalArgumentException("No existe el recurso en classpath: " + normalizedPath);
            }
            return classpathResource;
        }

        Path candidatePath = Paths.get(normalizedPath);
        if (!candidatePath.isAbsolute()) {
            candidatePath = Paths.get(System.getProperty("user.dir")).resolve(candidatePath).normalize();
        }

        if (Files.exists(candidatePath)) {
            return new FileSystemResource(candidatePath);
        }

        Resource classpathFallback = new ClassPathResource(normalizedPath);
        if (classpathFallback.exists()) {
            return classpathFallback;
        }

        throw new IllegalArgumentException("No existe el PDF en filesystem ni classpath: " + normalizedPath);
    }

    public String queryLLM(String question) {

        // Querying the vector store for documents related to the question
        List<Document> vectorStoreResult =
            vectorStore.doSimilaritySearch(SearchRequest.builder().query(question).topK(5)
                    .similarityThreshold(0.6).build());

        // Merging the documents into a single string
        String documents = vectorStoreResult.stream()
            .map(Document::getText)
            .collect(Collectors.joining(System.lineSeparator()));

        // Setting the prompt with the context
        String prompt = """
            Eres un asistente que responde preguntas sobre unas bases de terminos de referencia.
            Usa unicamente la informacion de la seccion DOCUMENTS para responder la QUESTION.
            Prioriza exactitud y claridad, especialmente para:
            - entregables
            - fechas, plazos y cronogramas
            - penalidades, multas y condiciones de incumplimiento

            Reglas:
            1) No inventes informacion ni supongas datos no presentes en el texto.
            2) Si falta evidencia suficiente, responde exactamente: "No tengo informacion suficiente en el documento".
            3) Si la pregunta combina varios puntos, responde por partes.
            4) Cuando sea posible, incluye el fragmento o condicion relevante de forma breve.
            
            DOCUMENTS:
            """ + documents
            + """
            QUESTION:
            """ + question;


        // Calling the chat model with the question
        String response = chatClient.prompt()
            .user(prompt)
            .call()
            .content();

        return response +
            System.lineSeparator() +
            "Found at page: " +
            // Retrieving the first ranked page number from the document metadata
            vectorStoreResult.get(0).getMetadata().get(PagePdfDocumentReader.METADATA_START_PAGE_NUMBER) +
            " of the manual";
    }
}
