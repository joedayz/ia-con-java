package com.example.boardgamebuddy;

public class ModerationException extends RuntimeException {
  public ModerationException(String category) {
    super(String.format(
        "Moderation failed. Content identified as %s.", category));
  }
}
