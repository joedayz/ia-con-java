package com.example.simpletranslator;

public record Translation(
    String text,
    String sourceLanguage,
    String targetLanguage) { }
