package com.senevilabs.supportagent.classification;

public interface TicketClassifier {
    TicketClassification classify(String subject, String body);
}
