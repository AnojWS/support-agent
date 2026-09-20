package com.senevilabs.supportagent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "spring.ai.google.genai.embedding.api-key=dummy-key-for-tests")
@Import(TestcontainersConfiguration.class)
class SupportAgentApplicationTests {

    @MockitoBean
    ChatModel chatModel;

    @MockitoBean
    EmbeddingModel embeddingModel;

    @MockitoBean
    VectorStore vectorStore;

    @Test
    void contextLoads() {
    }

}