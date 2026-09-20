package com.senevilabs.supportagent.web;

import com.senevilabs.supportagent.ticket.Ticket;
import com.senevilabs.supportagent.ticket.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        Ticket ticket = ticketService.create(request.subject(), request.body(), request.customerEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponse.from(ticket));
    }
}
