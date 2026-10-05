package com.senevilabs.supportagent.resolution;

public interface TicketResolver {
    TicketResolution resolve(String subject, String body, String kbContext);
}