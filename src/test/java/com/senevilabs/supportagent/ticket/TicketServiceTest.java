package com.senevilabs.supportagent.ticket;

import com.senevilabs.supportagent.classification.TicketClassification;
import com.senevilabs.supportagent.classification.TicketClassifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TicketServiceTest {

    private final TicketRepository tickets = mock(TicketRepository.class);
    private final TicketClassifier classifier = mock(TicketClassifier.class);
    private final TicketService service = new TicketService(tickets, classifier);

    @Test
    void classifiesAndTriagesNewTicket() {
        when(tickets.save(any(Ticket.class))).thenAnswer(invocation -> snapshot(invocation.getArgument(0)));
        when(classifier.classify("Server down", "The CRM is unreachable"))
                .thenReturn(new TicketClassification(TicketCategory.OUTAGE, TicketPriority.HIGH, "CRM is unreachable"));

        Ticket result = service.create("Server down", "The CRM is unreachable", "customer@example.com");

        assertThat(result.getStatus()).isEqualTo(TicketStatus.TRIAGED);
        assertThat(result.getCategory()).isEqualTo(TicketCategory.OUTAGE);
        assertThat(result.getPriority()).isEqualTo(TicketPriority.HIGH);
        assertThat(result.getSummary()).isEqualTo("CRM is unreachable");

        verify(classifier).classify("Server down", "The CRM is unreachable");
        verify(tickets, times(2)).save(any(Ticket.class));
    }

    @Test
    void savesTicketAsNewBeforeClassifying() {
        List<TicketStatus> statusesOnSave = new ArrayList<>();
        when(tickets.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket saved = invocation.getArgument(0);
            statusesOnSave.add(saved.getStatus());
            return saved;
        });
        when(classifier.classify(anyString(), anyString()))
                .thenReturn(new TicketClassification(TicketCategory.ACCESS, TicketPriority.LOW, "stub"));

        service.create("Login issue", "Cannot reset password", "user@example.com");

        assertThat(statusesOnSave).containsExactly(TicketStatus.NEW, TicketStatus.TRIAGED);
    }

    private Ticket snapshot(Ticket source) {
        Ticket copy = new Ticket();
        copy.setId(source.getId());
        copy.setSubject(source.getSubject());
        copy.setBody(source.getBody());
        copy.setCustomerEmail(source.getCustomerEmail());
        copy.setStatus(source.getStatus());
        copy.setPriority(source.getPriority());
        copy.setCategory(source.getCategory());
        copy.setSummary(source.getSummary());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }

}
