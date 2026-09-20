package com.senevilabs.supportagent.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotBlank @Size(max = 200) String subject,
        @NotBlank String body,
        @Email @NotBlank String customerEmail) {
}
