package com.senevilabs.supportagent.web;

import com.senevilabs.supportagent.ticket.Ticket;
import com.senevilabs.supportagent.ticket.TicketCategory;
import com.senevilabs.supportagent.ticket.TicketPriority;
import com.senevilabs.supportagent.ticket.TicketService;
import com.senevilabs.supportagent.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TicketService ticketService;

    @Test
    void createsTicketAndReturnsTriagedTicket() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSubject("Server down");
        ticket.setBody("The CRM is unreachable");
        ticket.setCustomerEmail("customer@example.com");
        ticket.setStatus(TicketStatus.TRIAGED);
        ticket.setCategory(TicketCategory.OUTAGE);
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setSummary("CRM is unreachable");
        ticket.setCreatedAt(Instant.now());
        ticket.setUpdatedAt(Instant.now());

        when(ticketService.create(anyString(), anyString(), anyString())).thenReturn(ticket);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"subject":"Server down","body":"The CRM is unreachable","customerEmail":"customer@example.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("TRIAGED"))
                .andExpect(jsonPath("$.category").value("OUTAGE"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.customerEmail").value("customer@example.com"));
    }

    @Test
    void rejectsBlankSubject() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"subject":"  ","body":"details","customerEmail":"customer@example.com"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"subject":"x","body":"details","customerEmail":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
