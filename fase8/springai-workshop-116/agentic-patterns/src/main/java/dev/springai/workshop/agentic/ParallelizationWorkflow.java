package dev.springai.workshop.agentic;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Paralelización de llamadas LLM (mismo prompt, varias entradas) o tareas independientes.
 * Basado en {@code com.example.agentic.ParallelizationlWorkflow} de spring-ai-examples.
 */
public class ParallelizationWorkflow {

    private final ChatClient chatClient;

    public ParallelizationWorkflow(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Mismo prompt para cada entrada (patrón "sectioning" de la guía).
     */
    public List<String> parallel(String prompt, List<String> inputs, int nWorkers) {
        Assert.notNull(prompt, "Prompt cannot be null");
        Assert.notEmpty(inputs, "Inputs list cannot be empty");
        Assert.isTrue(nWorkers > 0, "Number of workers must be greater than 0");

        ExecutorService executor = Executors.newFixedThreadPool(nWorkers);
        try {
            List<CompletableFuture<String>> futures = inputs.stream()
                    .map(input -> CompletableFuture.supplyAsync(() ->
                            chatClient.prompt(prompt + "\nInput: " + input).call().content(), executor))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Tareas independientes en paralelo (p. ej. varios agentes con distinto {@link ChatClient}).
     */
    public <T> List<T> parallelExecute(List<Callable<T>> tasks, int nWorkers) {
        Assert.notEmpty(tasks, "Tasks list cannot be empty");
        Assert.isTrue(nWorkers > 0, "Number of workers must be greater than 0");

        ExecutorService executor = Executors.newFixedThreadPool(nWorkers);
        try {
            List<CompletableFuture<T>> futures = tasks.stream()
                    .map(task -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return task.call();
                        } catch (Exception e) {
                            throw new RuntimeException("Parallel task failed", e);
                        }
                    }, executor))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        } finally {
            executor.shutdown();
        }
    }
}
