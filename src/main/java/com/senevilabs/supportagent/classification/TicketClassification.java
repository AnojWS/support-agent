package com.senevilabs.supportagent.classification;

import com.senevilabs.supportagent.ticket.TicketCategory;
import com.senevilabs.supportagent.ticket.TicketPriority;

public record TicketClassification(
        TicketCategory category,
        TicketPriority priority,
        String summary
) {
}
