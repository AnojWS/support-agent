package com.senevilabs.supportagent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SupportAgentApplicationTests {

	@Test
	void contextLoads() {
	}

}
