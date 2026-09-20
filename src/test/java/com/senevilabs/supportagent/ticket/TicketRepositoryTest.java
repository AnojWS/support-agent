package com.senevilabs.supportagent.ticket;

import com.senevilabs.supportagent.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class TicketRepositoryTest {

    @Autowired
    TicketRepository tickets;

    @Test
    void savesTicketWithDefaultsAndTimestamps() {
        Ticket saved = tickets.save(ticket("Cannot log in"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(TicketStatus.NEW);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isEqualTo(saved.getCreatedAt());
        assertThat(saved.getPriority()).isNull();

    }

    @Test
    void findsByStatus() {
        tickets.save(ticket("Billing question"));

        Ticket triaged = ticket("Server down");
        triaged.setStatus(TicketStatus.TRIAGED);
        triaged.setPriority(TicketPriority.URGENT);
        tickets.save(triaged);

        List<Ticket> untriaged = tickets.findByStatus(TicketStatus.NEW);

        assertThat(untriaged).hasSize(1);
        assertThat(untriaged.getFirst().getSubject()).isEqualTo("Billing question");
        assertThat(tickets.countByStatus(TicketStatus.TRIAGED)).isEqualTo(1);
    }

    private Ticket ticket(String subject) {
        Ticket t = new Ticket();
        t.setSubject(subject);
        t.setBody("Details of the problem.");
        t.setCustomerEmail("tester@example.com");
        return t;
    }
}
