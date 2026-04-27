package com.example.boardgamebuddy;

public record Question(
    String gameTitle,
    String question,
    String language) {

    public static final String DEFAULT_LANGUAGE = "English";

    public Question {
        if (language == null || language.isBlank()) {
            language = DEFAULT_LANGUAGE;
        }
    }

    public Question(String gameTitle, String question) {
        this(gameTitle, question, DEFAULT_LANGUAGE);
    }

}
