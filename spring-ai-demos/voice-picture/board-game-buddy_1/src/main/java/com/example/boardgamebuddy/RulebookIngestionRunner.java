package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RulebookIngestionRunner implements ApplicationRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(RulebookIngestionRunner.class);

  private final VectorStore vectorStore;

  RulebookIngestionRunner(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Override
  public void run(ApplicationArguments args) {
    var docs = List.of(
        rule("burger_battle", """
            In Burger Battle, the graveyard is the shared discard pile.
            Ingredient cards, traps, and most one-time effects are placed in the graveyard after they are resolved.
            If a card says to return something from the graveyard, it means this shared discard pile.
            """),
        rule("azul", """
            In Azul, players draft tiles from factories and place them on pattern lines.
            Completed pattern lines move one tile to the wall and score points.
            Extra tiles go to the floor line and cause penalty points.
            """),
        rule("carcassonne", """
            In Carcassonne, players place one landscape tile and may place one meeple each turn.
            Features score when completed, and meeples return to the player after scoring.
            Farmers usually score only at game end.
            """),
        rule("catan", """
            In Catan, players collect resources from adjacent terrain based on dice rolls.
            They spend resources to build roads, settlements, and cities.
            First player to the victory point target wins.
            """),
        rule("scythe", """
            In Scythe, players choose one action section each turn and resolve top then optional bottom actions.
            Combat, popularity, and objective stars contribute to endgame scoring.
            The game ends when a player places their sixth star.
            """),
        rule("puerto_rico", """
            In Puerto Rico, players select roles that everyone performs in clockwise order.
            Buildings and plantations create production engines and victory points.
            Colonists are required to activate most buildings.
            """),
        rule("7_wonders", """
            In 7 Wonders, players draft one card per age from hands passed around the table.
            Military conflicts are resolved at the end of each age with neighboring players.
            The player with the most victory points after three ages wins.
            """));

    vectorStore.add(docs);
    LOGGER.info("Loaded {} board game rule documents into the vector store.", docs.size());
  }

  private Document rule(String gameTitle, String text) {
    return Document.builder()
        .text(text.strip())
        .metadata(Map.of(
            "gameTitle", gameTitle,
            "docType", "rules"))
        .build();
  }
}

