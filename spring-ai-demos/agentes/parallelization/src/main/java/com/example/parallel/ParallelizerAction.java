package com.example.parallel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ParallelizerAction implements Action {

  private static final Logger LOGGER = Logger.getLogger(ParallelizerAction.class.getName());

  private final List<Action> workerActions;

  public ParallelizerAction(List<Action> workerActions) {
    this.workerActions = workerActions; 
  }
  
  @Override
  public String act(String input) {
    LOGGER.info("Starting parallel action...");
    ExecutorService executor = Executors
        .newFixedThreadPool(workerActions.size());  

    var futures = workerActions.stream()
        .map(worker -> CompletableFuture.supplyAsync(() -> {
          return worker.act(input);
        }, executor))
        .toList();   

    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
        futures.toArray(CompletableFuture[]::new));
    allFutures.join();  

    return futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.joining("\n-----\n"));  
  }

}
