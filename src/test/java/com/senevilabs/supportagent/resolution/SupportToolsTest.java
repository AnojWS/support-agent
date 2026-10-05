package com.senevilabs.supportagent.resolution;

import com.senevilabs.supportagent.knowledge.KnowledgeBaseService;
import com.senevilabs.supportagent.ops.ServiceCatalog;
import com.senevilabs.supportagent.ops.ServiceOperator;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupportToolsTest {

    private final ServiceOperator operator = mock(ServiceOperator.class);
    private final ServiceCatalog catalog = new ServiceCatalog();
    private final KnowledgeBaseService kb = mock(KnowledgeBaseService.class);
    private final SupportTools tools = new SupportTools(operator, catalog, kb);

    @Test
    void delegatesHealthCheckForAllowedService() {
        catalog.setDockerContainers(List.of("vpn-db"));
        when(operator.check("vpn-db")).thenReturn("Docker status of vpn-db: running");

        assertThat(tools.checkHealth("vpn-db")).isEqualTo("Docker status of vpn-db: running");

        verify(operator).check("vpn-db");
    }

    @Test
    void delegatesRestartForAllowedService() {
        catalog.setDockerContainers(List.of("vpn-db"));
        when(operator.restart("vpn-db")).thenReturn("Restarted vpn-db");

        assertThat(tools.restartService("vpn-db")).isEqualTo("Restarted vpn-db");

        verify(operator).restart("vpn-db");
    }

    @Test
    void refusesUnknownServiceWithoutInvokingOperator() {
        catalog.setDockerContainers(List.of("vpn-db"));

        String result = tools.checkHealth("prod-db");

        assertThat(result).isEqualTo("Unknown service 'prod-db'. Available services: vpn-db");
        verify(operator, never()).check(anyString());
        verify(operator, never()).restart(anyString());
    }

    @Test
    void refusesUnknownServiceRestartWithoutInvokingOperator() {
        catalog.setDockerContainers(List.of("vpn-db"));

        String result = tools.restartService("prod-db");

        assertThat(result).contains("Unknown service 'prod-db'").contains("vpn-db");
        verify(operator, never()).restart(anyString());
    }

    @Test
    void refusesEverythingWhenCatalogIsEmpty() {
        String result = tools.restartService("vpn-db");

        assertThat(result)
                .isEqualTo("Unknown service 'vpn-db'. Available services: (none configured)");
        verify(operator, never()).restart(anyString());
    }

    @Test
    void joinsKnowledgeBaseHits() {
        when(kb.search("vpn")).thenReturn(List.of(
                new Document("Restarting the VPN container fixes the tunnel"),
                new Document("Check the gateway status first")));

        assertThat(tools.searchKnowledgeBase("vpn"))
                .isEqualTo("Restarting the VPN container fixes the tunnel\n\nCheck the gateway status first");
    }
}