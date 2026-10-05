package com.senevilabs.supportagent.resolution;

import com.senevilabs.supportagent.ops.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LlmTicketResolverTest {

    @Test
    void returnsModelAnswerGroundedInKbContext() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());

        AtomicReference<Prompt> sentPrompt = new AtomicReference<>();
        when(chatModel.call(any(Prompt.class))).thenAnswer(invocation -> {
            sentPrompt.set(invocation.getArgument(0));
            return new ChatResponse(List.of(new Generation(new AssistantMessage(
                    "Checked vpn-db, found it exited, restarted it and the tunnel is back up."))));
        });

        ServiceCatalog catalog = new ServiceCatalog();
        catalog.setDockerContainers(List.of("vpn-db"));

        LlmTicketResolver resolver = new LlmTicketResolver(
                ChatClient.builder(chatModel),
                catalog,
                new SupportTools(null, catalog, null));

        TicketResolution result = resolver.resolve(
                "VPN is down",
                "Cannot connect to the VPN",
                "Restarting the VPN container fixes the tunnel");

        assertThat(result.content())
                .isEqualTo("Checked vpn-db, found it exited, restarted it and the tunnel is back up.");
        assertThat(sentPrompt.get().getContents())
                .contains("Restarting the VPN container fixes the tunnel");
        assertThat(sentPrompt.get().getContents()).contains("vpn-db");
    }
}