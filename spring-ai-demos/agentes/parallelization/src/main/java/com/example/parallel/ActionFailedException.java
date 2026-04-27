package com.example.parallel;

public class ActionFailedException extends RuntimeException {
  public ActionFailedException(String message) {
    super(message);
  }
}
