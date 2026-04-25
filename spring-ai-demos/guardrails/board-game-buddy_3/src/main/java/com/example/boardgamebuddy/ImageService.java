package com.example.boardgamebuddy;

public interface ImageService {

  String generateImageForUrl(String instructions);

  byte[] generateImageForImageBytes(String instructions);

}
