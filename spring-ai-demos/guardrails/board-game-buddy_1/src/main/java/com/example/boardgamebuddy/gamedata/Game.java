package com.example.boardgamebuddy.gamedata;

import org.springframework.data.annotation.Id;

public record Game(
    @Id Long id,
    String slug,
    String title,
    float complexity) {

  public GameComplexity complexityEnum() {
    var rounded = Math.round(complexity);
    return GameComplexity.values()[rounded];
  }

}
