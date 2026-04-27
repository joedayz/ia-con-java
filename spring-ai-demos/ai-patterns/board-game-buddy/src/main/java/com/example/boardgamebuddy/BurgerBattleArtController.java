package com.example.boardgamebuddy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BurgerBattleArtController {

  private final BoardGameService boardGameService;
  private final ImageService imageService;

  public BurgerBattleArtController(BoardGameService boardGameService,
                                   ImageService imageService) {  
    this.boardGameService = boardGameService;
    this.imageService = imageService;
  }

  @GetMapping(path="/burgerBattleArt")
  public String burgerBattleArt(@RequestParam("burger") String burger) {
    var instructions = getImageInstructions(burger);
    return imageService.generateImageForUrl(instructions); 
  }

  @GetMapping(path="/burgerBattleArt", produces = "image/png")
    public byte[] burgerBattleArtImage(@RequestParam("burger") String burger) {
    var instructions = getImageInstructions(burger);
    return imageService.generateImageForImageBytes(instructions); 
  }

  private String getImageInstructions(String burger) {
    var question = new Question(
        "Burger Battle",
        "What ingredients are on the " + burger + " burger?");
    var answer = boardGameService.askQuestion(
        question, "art_conversation");        

    return "A burger called " + burger + " " +
        "with the following ingredients: " + answer.answer() + ". " +
        "Style the background to match the name of the burger."; 
  }

}
