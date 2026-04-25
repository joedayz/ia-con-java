package com.example.boardgamebuddy;

import org.springframework.core.io.Resource;

public interface BoardGameService {
    Answer askQuestion(Question question, String conversationId);

    Answer askQuestion(Question question,
                              Resource image,
                              String imageContentType,
                              String conversationId); 
}
