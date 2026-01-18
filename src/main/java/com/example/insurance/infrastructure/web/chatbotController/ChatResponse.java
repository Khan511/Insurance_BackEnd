package com.example.insurance.infrastructure.web.chatbotController;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {

    private String response;
    private boolean fromAI;
}
