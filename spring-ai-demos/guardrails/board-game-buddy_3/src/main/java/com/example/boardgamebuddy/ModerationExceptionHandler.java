package com.example.boardgamebuddy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice     
public class ModerationExceptionHandler {

  @ExceptionHandler(ModerationException.class)   
  public ProblemDetail moderationException(ModerationException ex) {
    var problemDetail = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()); 
    problemDetail.setTitle("Moderation Exception");
    return problemDetail;
  }

}
