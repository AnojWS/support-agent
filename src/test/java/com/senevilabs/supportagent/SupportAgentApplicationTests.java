package com.senevilabs.supportagent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SupportAgentApplicationTests {

	@MockitoBean
    ChatModel chatModel;

	@Test
	void contextLoads() {
	}

}
