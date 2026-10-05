package com.senevilabs.supportagent.resolution;

import com.senevilabs.supportagent.ops.ServiceCatalog;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LlmTicketResolver implements TicketResolver {

    private final ChatClient chatClient;
    private final ServiceCatalog catalog;

    public LlmTicketResolver(ChatClient.Builder builder, ServiceCatalog catalog, SupportTools tools) {
        this.catalog = catalog;
        this.chatClient = builder
                .defaultTools(tools)
                .build();
    }

    @Override
    public TicketResolution resolve(String subject, String body, String kbContext) {
        String answer = chatClient.prompt()
                .system(s -> s.text("""
                        You are an IT support resolution agent.
                        The only services you may touch are: {services}.
                        NEVER reference or restart anything not in that list.
                        Use restartService only after checkHealth shows unhealthy.
                        If you cannot resolve the issue, say so and recommend escalation to a human engineer.
                        Answer in plain English, 1-3 sentences: what you checked, what you did, and the current status.
                        """)
                        .param("services", catalog.describeDockerContainers()))
                .user(u -> u.text("""
                        ## Internal Knowledge
                        {kbContext}

                        ## Ticket
                        Subject: {subject}

                        Body:
                        {body}
                        """)
                        .param("kbContext", kbContext)
                        .param("subject", subject)
                        .param("body", body))
                .call()
                .content();
        return new TicketResolution(answer);
    }
}