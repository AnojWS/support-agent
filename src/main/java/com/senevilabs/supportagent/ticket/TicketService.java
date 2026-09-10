package com.senevilabs.supportagent.ticket;

import com.senevilabs.supportagent.classification.TicketClassification;
import com.senevilabs.supportagent.classification.TicketClassifier;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final TicketRepository tickets;
    private final TicketClassifier classifier;

    public TicketService(TicketRepository tickets, TicketClassifier classifier){
        this.tickets = tickets;
        this.classifier = classifier;
    }

    public Ticket create(String subject, String body, String customerEmail) {
        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setBody(body);
        ticket.setCustomerEmail(customerEmail);
        ticket.setStatus(TicketStatus.NEW);

        tickets.save(ticket);

        TicketClassification classification = classifier.classify(subject, body);

        ticket.setCategory(classification.category());
        ticket.setPriority(classification.priority());
        ticket.setSummary(classification.summary());
        ticket.setStatus(TicketStatus.TRIAGED);

        return tickets.save(ticket);
    }
}

