package com.example.boardgamebuddy;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SummaryController {

  private final BoardGameService boardGameService;

  public SummaryController(BoardGameService boardGameService) { 
    this.boardGameService = boardGameService;
  }

  @PostMapping("/summarize")
  public Answer summarize(
      @RequestPart("rulesDocument") MultipartFile rulesDocument) { 

    var reader = new TikaDocumentReader(rulesDocument.getResource());
    var rulesText = reader.get().getFirst().getText();     

    return boardGameService.summarizeRules(rulesText);  
  }

}
