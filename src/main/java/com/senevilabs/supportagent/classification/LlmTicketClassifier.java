package com.senevilabs.supportagent.classification;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LlmTicketClassifier implements TicketClassifier {

    private final ChatClient chatClient;

    public LlmTicketClassifier(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        You are an IT support triage agent.
                        Classify each support ticket by:
                        - category: exactly one of BILLING, ACCESS, OUTAGE, HARDWARE, SOFTWARE, OTHER
                        - priority: exactly one of LOW, MEDIUM, HIGH, URGENT
                        - summary: a concise 1-2 sentence summary of the problem.
                        """)
                .build();
    }

    public TicketClassification classify(String subject, String body) {
        return chatClient.prompt()
                .user("Subject: " + subject + "\n\nBody:\n" + body)
                .call()
                .entity(TicketClassification.class);
    }
}
