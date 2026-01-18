
package com.example.insurance.infrastructure.web.chatbotController;

import com.example.insurance.domain.chatbot.service.ChatBotService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatBotService chatService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat request: {}", request.getMessage());
        log.info("Conversation ID: {}", request.getConversationId());

        try {
            ChatResponse response = chatService.processMessage(request);
            log.info("Response generated: {}",
                    response.getResponse().substring(0, Math.min(50, response.getResponse().length())));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing chat request: ", e);
            ChatResponse fallback = new ChatResponse(
                    "I apologize, but I'm having trouble processing your request. " +
                            "Please try again or contact our support team at +1-800-555-INSURANCE.",
                    false);
            return ResponseEntity.status(HttpStatus.OK).body(fallback);
        }
    }

    // Additional endpoints for RTK Query
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(
                Map.of(
                        "status", "online",
                        "model", "insurance-assistant-v1",
                        "timestamp", System.currentTimeMillis()));
    }

    @GetMapping("/quick-responses")
    public ResponseEntity<List<String>> getQuickResponses() {
        List<String> responses = Arrays.asList(
                "How to file a claim?",
                "What insurance plans do you offer?",
                "Need 24/7 support",
                "Policy renewal process",
                "Calculate premium",
                "Network hospitals",
                "Document requirements",
                "Claim processing time",
                "Premium payment options",
                "Add family member to policy");
        return ResponseEntity.ok(responses);
    }
}
