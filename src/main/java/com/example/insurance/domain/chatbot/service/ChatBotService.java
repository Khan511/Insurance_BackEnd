package com.example.insurance.domain.chatbot.service;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import com.example.insurance.infrastructure.web.chatbotController.ChatRequest;
// import com.example.insurance.infrastructure.web.chatbotController.ChatResponse;

// import java.util.*;

// @Service
// public class ChatBotService {

//     @Value("${ai.huggingface.api.key}")
//     private String apiKey;

//     @Value("${ai.huggingface.api.url}")
//     private String apiUrl;

//     private final RestTemplate restTemplate;
//     private final List<String> insuranceContext;

//     public ChatBotService() {
//         this.restTemplate = new RestTemplate();
//         // Pre-load insurance knowledge
//         this.insuranceContext = Arrays.asList(
//                 "Our company offers life insurance, health insurance, and car insurance.",
//                 "To file a claim, visit the Claims section and upload required documents.",
//                 "Policy renewal can be done online 30 days before expiry.",
//                 "24/7 support is available at +1-800-INSURANCE.",
//                 "We provide coverage for hospitalization, surgeries, and medication.",
//                 "Premium depends on age, health condition, and coverage amount.",
//                 "You can add family members to your health insurance policy.",
//                 "Cashless hospitalization available at 5000+ network hospitals.",
//                 "Claim processing time is 7-14 working days.",
//                 "Document required: ID proof, address proof, medical reports.");
//     }

//     public ChatResponse processMessage(ChatRequest request) {
//         String userMessage = request.getMessage().toLowerCase();
//         String context = String.join(". ", insuranceContext);

//         // Check if it's a simple question that can be answered from context
//         String directAnswer = checkDirectAnswer(userMessage);
//         if (directAnswer != null) {
//             return new ChatResponse(directAnswer, false);
//         }

//         // If not a simple question, use AI
//         return getAIResponse(userMessage, context);
//     }

//     private String checkDirectAnswer(String userMessage) {
//         Map<String, String> qnaMap = new HashMap<>();
//         qnaMap.put("contact", "You can contact us at +1-800-INSURANCE or email support@insurance.com");
//         qnaMap.put("support", "24/7 support available. Call +1-800-INSURANCE or use Live Chat");
//         qnaMap.put("claim", "File claim at: Login → Claims → New Claim → Upload documents");
//         qnaMap.put("premium", "Premium depends on age, coverage, and medical history. Check calculator.");
//         qnaMap.put("policy", "We offer Health, Life, Auto, and Home insurance policies.");
//         qnaMap.put("renew", "Renew policy: Login → My Policies → Renew → Payment");
//         qnaMap.put("hospital", "5000+ network hospitals. Check 'Network Hospitals' section.");
//         qnaMap.put("document", "Required: ID proof, address proof, medical reports, claim form.");
//         qnaMap.put("time", "Claim processing: 7-14 days. Policy issuance: 3-5 days.");
//         qnaMap.put("coverage", "Coverage includes hospitalization, surgery, medication, ambulance.");

//         for (Map.Entry<String, String> entry : qnaMap.entrySet()) {
//             if (userMessage.contains(entry.getKey())) {
//                 return entry.getValue();
//             }
//         }
//         return null;
//     }

//     private ChatResponse getAIResponse(String userMessage, String context) {
//         try {
//             // Using Hugging Face Inference API (Free)
//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_JSON);
//             headers.setBearerAuth(apiKey);

//             // Use a smaller model that's free
//             Map<String, Object> requestBody = new HashMap<>();
//             requestBody.put("inputs",
//                     "Context: " + context + "\n" +
//                             "Question: " + userMessage + "\n" +
//                             "Answer as an insurance expert:");

//             HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

//             // Use a free model from Hugging Face
//             String modelUrl = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.1";

//             ResponseEntity<String> response = restTemplate.exchange(
//                     modelUrl,
//                     HttpMethod.POST,
//                     entity,
//                     String.class);

//             String aiResponse = response.getBody();
//             // Clean up the response
//             aiResponse = cleanResponse(aiResponse);

//             return new ChatResponse(aiResponse, true);

//         } catch (Exception e) {
//             // Fallback response if API fails
//             return new ChatResponse(
//                     "I apologize, but I'm having trouble processing your request. " +
//                             "Please contact our support team at +1-800-INSURANCE for immediate assistance.",
//                     false);
//         }
//     }

//     private String cleanResponse(String response) {
//         if (response == null)
//             return "I couldn't generate a response. Please try again.";

//         // Remove unwanted tokens and limit length
//         response = response.replaceAll("\\[INST\\].*\\[/INST\\]", "")
//                 .replaceAll("<s>|</s>", "")
//                 .replaceAll("\\n+", " ")
//                 .trim();

//         // Limit response length
//         if (response.length() > 500) {
//             response = response.substring(0, 500) + "...";
//         }

//         return response;
//     }
// }

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.insurance.infrastructure.web.chatbotController.ChatRequest;
import com.example.insurance.infrastructure.web.chatbotController.ChatResponse;

import java.util.*;

@Service
public class ChatBotService {

    @Value("${ai.huggingface.api.key}")
    private String apiKey;

    @Value("${ai.huggingface.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final Map<String, String> insuranceQnA;
    private final List<String> insuranceContext;

    public ChatBotService() {
        this.restTemplate = new RestTemplate();
        this.insuranceQnA = initializeQnAMap();

        // Pre-load insurance knowledge for AI context
        this.insuranceContext = Arrays.asList(
                "Company: Insurance Solutions Inc., offering comprehensive insurance services.",
                "Products: Health Insurance, Life Insurance, Car/Auto Insurance, Home Insurance.",
                "Claim Process: Login to portal → Go to Claims → File New Claim → Upload required documents → Submit.",
                "Contact: 24/7 support available at +1-800-555-INSURANCE or email support@insurancesolutions.com.",
                "Policy Renewal: Can be done online 30 days before expiry through customer portal.",
                "Premium Calculation: Based on age, health condition, coverage amount, and risk factors.",
                "Network Hospitals: Over 5000+ network hospitals across the country for cashless treatment.",
                "Document Requirements: ID Proof, Address Proof, Medical Reports, Claim Form, Policy Document.",
                "Claim Processing Time: Typically 7-14 working days for complete processing.",
                "Family Members: Can be added during enrollment or at renewal time.",
                "Payment Options: Credit Card, Debit Card, Net Banking, UPI, Auto-debit.",
                "Waiting Period: 30 days for general illnesses, 2-4 years for specific conditions.",
                "Coverage: Includes hospitalization, day care procedures, pre and post hospitalization, ambulance.",
                "Exclusions: Cosmetic surgery, self-inflicted injuries, war-related injuries, pre-existing conditions after waiting period.");
    }

    private Map<String, String> initializeQnAMap() {
        Map<String, String> qna = new HashMap<>();

        // Comprehensive Q&A mapping with multiple keywords
        qna.put("claim|file claim|how to claim|claim process",
                "To file a claim:\n1. Login to your account\n2. Go to 'Claims' section\n3. Click 'File New Claim'\n4. Fill the claim form\n5. Upload required documents (ID proof, medical reports, bills)\n6. Submit for review\n\nClaim processing time: 7-14 working days.");

        qna.put("policy|plans|insurance plans|coverage|products",
                "We offer the following insurance plans:\n\n1. **Health Insurance**\n   - Individual & Family Floater\n   - Senior Citizen Plans\n   - Critical Illness Cover\n   - Top-up Plans\n\n2. **Life Insurance**\n   - Term Plans\n   - Endowment Plans\n   - ULIPs\n   - Retirement Plans\n\n3. **Car/Auto Insurance**\n   - Comprehensive Cover\n   - Third-party Liability\n   - Own Damage Cover\n\n4. **Home Insurance**\n   - Building & Contents\n   - Natural Calamity Cover");

        qna.put("support|contact|help|emergency",
                "We're available 24/7 to help you:\n\n📞 **Phone Support**: +1-800-555-INSURANCE\n✉️ **Email**: support@insurancesolutions.com\n💬 **Live Chat**: Available on our website\n🏢 **Office Hours**: 9 AM - 7 PM (Mon-Sat)\n\n**Emergency Contact**: For immediate assistance during hospitalization, call our emergency helpline: +1-800-555-EMERGENCY");

        qna.put("premium|calculate premium|premium calculation|price|cost",
                "Premium calculation depends on several factors:\n\n**For Health Insurance**:\n- Age of insured\n- Sum insured amount\n- Medical history\n- City/Tier\n- Add-on covers\n\n**For Life Insurance**:\n- Age & Gender\n- Sum assured\n- Policy term\n- Smoking habits\n- Occupation type\n\n**Use our Premium Calculator** on the website for exact quotes or contact our sales team.");

        qna.put("renew|renewal|policy renewal",
                "Policy renewal process:\n\n**Online Renewal**:\n1. Login to customer portal\n2. Go to 'My Policies'\n3. Select policy to renew\n4. Review terms & premium\n5. Make payment\n\n**Auto-renewal Option**:\nYou can enable auto-debit for automatic renewal.\n\n**Grace Period**: 30 days from expiry date\n**Late Fees**: May apply after grace period");

        qna.put("hospital|network|network hospitals|cashless",
                "We have a wide network of 5000+ hospitals across the country.\n\n**To find network hospitals**:\n1. Visit our website → 'Network Hospitals'\n2. Search by city/pin code\n3. View hospital details & facilities\n\n**For cashless hospitalization**:\n1. Inform us 48 hours before planned admission\n2. For emergency, inform within 24 hours of admission\n3. Submit pre-authorization form\n4. Present health card at hospital");

        qna.put("document|documents|requirements|required documents",
                "**For New Policy**:\n- Identity Proof (Aadhaar/Passport/PAN)\n- Address Proof\n- Age Proof\n- Medical Reports (if applicable)\n- Passport-size photos\n\n**For Claims**:\n- Claim Form\n- Original Policy Document\n- Medical Certificate\n- Hospital Bills & Receipts\n- Investigation Reports\n- ID Proof of patient\n- Bank Details for settlement");

        qna.put("time|processing time|how long|duration",
                "Processing times:\n\n**Policy Issuance**: 3-5 working days\n**Claim Settlement**: 7-14 working days\n**Policy Renewal**: Instant (online)\n**Endorsements**: 2-3 working days\n**Surrender/Withdrawal**: 10-15 working days\n**Loan against Policy**: 5-7 working days");

        qna.put("family|add family|dependents|family member",
                "Adding family members to your policy:\n\n**Eligible Family Members**:\n- Spouse\n- Children (up to 25 years)\n- Parents (up to 85 years)\n- Parents-in-law\n\n**Process**:\n1. During enrollment: Add in application\n2. Mid-term: Submit 'Add Member' form\n3. At renewal: Include in renewal application\n\n**Documents Required**:\n- Family member's ID proof\n- Age proof\n- Relationship proof");

        qna.put("payment|pay|payment options|methods",
                "Available payment methods:\n\n**Online**:\n- Credit/Debit Cards (Visa/MasterCard/RuPay)\n- Net Banking\n- UPI (Google Pay/PhonePe/Paytm)\n- Digital Wallets\n\n**Offline**:\n- Cheque/DD\n- Cash (at branch)\n- Auto-debit\n\n**Payment Frequency**:\n- Annual\n- Semi-annual\n- Quarterly\n- Monthly (auto-debit only)");

        return qna;
    }

    public ChatResponse processMessage(ChatRequest request) {
        String userMessage = request.getMessage().toLowerCase().trim();

        // First, try to find direct answer
        String directAnswer = checkDirectAnswer(userMessage);
        if (directAnswer != null) {
            return new ChatResponse(directAnswer, false);
        }

        // If not found, use AI with better context
        String context = String.join(". ", insuranceContext);
        return getAIResponse(userMessage, context, request);
    }

    private String checkDirectAnswer(String userMessage) {
        for (Map.Entry<String, String> entry : insuranceQnA.entrySet()) {
            String[] keywords = entry.getKey().split("\\|");
            for (String keyword : keywords) {
                if (userMessage.contains(keyword)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private ChatResponse getAIResponse(String userMessage, String context, ChatRequest request) {
        try {
            // Fallback to simple rule-based response if AI fails
            if (userMessage.contains("thank") || userMessage.contains("thanks")) {
                return new ChatResponse(
                        "You're welcome! Is there anything else I can help you with regarding insurance?", false);
            }

            if (userMessage.contains("hello") || userMessage.contains("hi") || userMessage.contains("hey")) {
                return new ChatResponse(
                        "Hello! I'm your insurance assistant. I can help you with:\n- Policy information\n- Claim process\n- Premium queries\n- Network hospitals\n- Document requirements\n- Renewal process\n\nHow can I assist you today?",
                        false);
            }

            if (userMessage.contains("bye") || userMessage.contains("goodbye")) {
                return new ChatResponse(
                        "Goodbye! Feel free to reach out if you have any insurance-related questions. Have a great day! \uD83D\uDE0A",
                        false);
            }

            // If using Hugging Face API
            if (apiKey != null && !apiKey.isEmpty()) {
                return getHuggingFaceResponse(userMessage, context);
            }

            // Fallback generic response
            return new ChatResponse(
                    "I understand you're asking about: \"" + request.getMessage() + "\"\n\n" +
                            "As an insurance assistant, I can help you with:\n" +
                            "1. Policy details and coverage\n" +
                            "2. Claim filing process\n" +
                            "3. Premium calculation\n" +
                            "4. Network hospital information\n" +
                            "5. Document requirements\n" +
                            "6. Renewal procedures\n\n" +
                            "Could you please specify which insurance service you need help with?",
                    false);

        } catch (Exception e) {
            // Final fallback
            return new ChatResponse(
                    "Thank you for your question! For detailed assistance with insurance queries, " +
                            "please contact our customer support:\n\n" +
                            "📞 +1-800-555-INSURANCE\n" +
                            "✉️ support@insurancesolutions.com\n" +
                            "💬 Live Chat available on our website\n\n" +
                            "Our team is available 24/7 to help you.",
                    false);
        }
    }

    private ChatResponse getHuggingFaceResponse(String userMessage, String context) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs",
                    "You are an insurance expert assistant. Use the following context to answer the question.\n\n" +
                            "Context: " + context + "\n\n" +
                            "Question: " + userMessage + "\n\n" +
                            "Provide a helpful, accurate response about insurance in a friendly tone. " +
                            "If you don't know something, suggest contacting customer support.");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // You might want to use a smaller, faster model
            String modelUrl = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.1";

            ResponseEntity<String> response = restTemplate.exchange(
                    modelUrl,
                    HttpMethod.POST,
                    entity,
                    String.class);

            String aiResponse = response.getBody();
            aiResponse = cleanResponse(aiResponse);

            return new ChatResponse(aiResponse, true);

        } catch (Exception e) {
            // Return helpful fallback if API fails
            return new ChatResponse(
                    "I'm currently having trouble accessing detailed information. " +
                            "For immediate assistance, please:\n\n" +
                            "1. Visit our FAQ section at https://insurancesolutions.com/faq\n" +
                            "2. Call our support: +1-800-555-INSURANCE\n" +
                            "3. Email: support@insurancesolutions.com\n\n" +
                            "Is there anything specific I can help you with from our common queries?",
                    false);
        }
    }

    private String cleanResponse(String response) {
        if (response == null)
            return "I apologize, I couldn't generate a response at the moment.";

        // Clean up the response
        response = response.replaceAll("\\[INST\\].*\\[/INST\\]", "")
                .replaceAll("<s>|</s>|\\[\\/?\\w+\\]", "")
                .replaceAll("\\n+", "\n")
                .replaceAll("\\s+", " ")
                .trim();

        // Ensure response is not empty
        if (response.isEmpty()) {
            return "I apologize, but I couldn't generate a proper response. Please try rephrasing your question or contact our support team for assistance.";
        }

        // Limit length but ensure complete sentences
        if (response.length() > 1000) {
            int lastPeriod = response.substring(0, 1000).lastIndexOf(".");
            response = response.substring(0, lastPeriod + 1);
        }

        return response;
    }
}