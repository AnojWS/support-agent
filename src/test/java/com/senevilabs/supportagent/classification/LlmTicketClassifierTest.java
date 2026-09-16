package com.senevilabs.supportagent.classification;

import com.senevilabs.supportagent.ticket.TicketCategory;
import com.senevilabs.supportagent.ticket.TicketPriority;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LlmTicketClassifierTest {

    @Test
    void parsesModelResponseIntoClassification() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());

        String json = """
                {"category":"OUTAGE","priority":"HIGH","summary":"The billing server is down."}
                """;

        when(chatModel.call(any(Prompt.class))).thenReturn(new ChatResponse(
                java.util.List.of(new Generation(new AssistantMessage(json)))));

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        LlmTicketClassifier classifier = new LlmTicketClassifier(builder);

        TicketClassification result = classifier.classify("Billing down", "Invoice page 500s");

        assertThat(result.category()).isEqualTo(TicketCategory.OUTAGE);
        assertThat(result.priority()).isEqualTo(TicketPriority.HIGH);
        assertThat(result.summary()).isEqualTo("The billing server is down.");
    }
}
