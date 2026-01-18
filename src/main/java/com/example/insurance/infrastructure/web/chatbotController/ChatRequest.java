package com.example.insurance.infrastructure.web.chatbotController;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String conversationId;
    private String context;
}