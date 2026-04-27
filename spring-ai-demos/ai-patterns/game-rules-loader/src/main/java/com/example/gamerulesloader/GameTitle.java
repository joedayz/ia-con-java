package com.example.gamerulesloader;

public record GameTitle(String title) {

  public String getNormalizedTitle() {
    return title.toLowerCase().replace(" ", "_");
  }

}
