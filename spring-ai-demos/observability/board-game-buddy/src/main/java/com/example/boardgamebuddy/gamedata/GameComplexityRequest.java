package com.example.boardgamebuddy.gamedata;

import org.springframework.context.annotation.Description;

@Description("Request data about a game, given the game title.")
public record GameComplexityRequest(String title) {
}
