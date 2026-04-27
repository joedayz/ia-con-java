package com.example.sentimentanalysis;

public record SentimentAnalysis(
    String text,
    double score,
    String explanation) { }
