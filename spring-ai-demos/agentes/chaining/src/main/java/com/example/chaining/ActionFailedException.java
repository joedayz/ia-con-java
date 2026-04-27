package com.example.chaining;

public class ActionFailedException extends RuntimeException {
  public ActionFailedException(String message) {
    super(message);
  }
}
