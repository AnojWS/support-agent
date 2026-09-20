package com.senevilabs.supportagent.web;

import com.senevilabs.supportagent.ticket.Ticket;
import com.senevilabs.supportagent.ticket.TicketCategory;
import com.senevilabs.supportagent.ticket.TicketPriority;
import com.senevilabs.supportagent.ticket.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        Long id,
        String subject,
        String body,
        String customerEmail,
        TicketStatus status,
        TicketCategory category,
        TicketPriority priority,
        String summary,
        Instant createdAt,
        Instant updatedAt) {

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getBody(),
                ticket.getCustomerEmail(),
                ticket.getStatus(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getSummary(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt());
    }
}
