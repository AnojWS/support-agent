package com.senevilabs.supportagent.ticket;

import com.senevilabs.supportagent.classification.TicketClassification;
import com.senevilabs.supportagent.classification.TicketClassifier;
import com.senevilabs.supportagent.knowledge.KnowledgeBaseService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private final TicketRepository tickets;
    private final TicketClassifier classifier;
    private final KnowledgeBaseService kb;

    public TicketService(TicketRepository tickets, TicketClassifier classifier, KnowledgeBaseService kb) {
        this.tickets = tickets;
        this.classifier = classifier;
        this.kb = kb;
    }

    public Ticket create(String subject, String body, String customerEmail) {
        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setBody(body);
        ticket.setCustomerEmail(customerEmail);
        ticket.setStatus(TicketStatus.NEW);

        tickets.save(ticket);

        String context = kb.search(subject + " " + body).stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        TicketClassification classification = classifier.classify(subject, body, context);

        ticket.setCategory(classification.category());
        ticket.setPriority(classification.priority());
        ticket.setSummary(classification.summary());
        ticket.setStatus(TicketStatus.TRIAGED);

        return tickets.save(ticket);
    }
}

