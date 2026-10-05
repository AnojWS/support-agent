package com.senevilabs.supportagent.ops.docker;

import com.senevilabs.supportagent.ops.CommandResult;
import com.senevilabs.supportagent.ops.CommandRunner;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DockerServiceOperatorTest {

    private final CommandRunner runner = mock(CommandRunner.class);
    private final DockerServiceOperator operator = new DockerServiceOperator(runner);

    @Test
    void reportsContainerStatusFromInspect() {
        when(runner.run("docker", "inspect", "--format", "{{.State.Status}}", "vpn-db"))
                .thenReturn(new CommandResult(0, "running"));

        assertThat(operator.check("vpn-db")).isEqualTo("Docker status of vpn-db: running");
    }

    @Test
    void reportsInspectFailure() {
        when(runner.run("docker", "inspect", "--format", "{{.State.Status}}", "vpn-db"))
                .thenReturn(new CommandResult(1, "Error: No such object: vpn-db"));

        assertThat(operator.check("vpn-db"))
                .contains("Could not inspect vpn-db")
                .contains("No such object");
    }

    @Test
    void reportsRestartSuccess() {
        when(runner.run("docker", "restart", "vpn-db")).thenReturn(new CommandResult(0, "vpn-db"));

        assertThat(operator.restart("vpn-db")).isEqualTo("Restarted vpn-db");
    }

    @Test
    void reportsRestartFailure() {
        when(runner.run("docker", "restart", "vpn-db"))
                .thenReturn(new CommandResult(1, "Error response from daemon: container not running"));

        assertThat(operator.restart("vpn-db"))
                .contains("Restart failed for vpn-db")
                .contains("container not running");
    }
}