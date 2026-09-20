package com.senevilabs.supportagent.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    public record PingResponse(String status, String service) {}

    @GetMapping("/api/ping")
    public PingResponse ping() {
        return new PingResponse("ok", "support-agent");
    }
}
