package com.senevilabs.supportagent.ops.docker;

import com.senevilabs.supportagent.ops.CommandResult;
import com.senevilabs.supportagent.ops.CommandRunner;
import com.senevilabs.supportagent.ops.ServiceOperator;

public class DockerServiceOperator implements ServiceOperator {

    private final CommandRunner runner;

    public DockerServiceOperator(CommandRunner runner) {
        this.runner = runner;
    }

    @Override
    public String check(String service) {
        CommandResult result = runner.run("docker", "inspect", "--format", "{{.State.Status}}", service);
        return result.exitCode() == 0
                ? "Docker status of " + service + ": " + result.stdout()
                : "Could not inspect " + service + ": " + result.stdout();
    }

    @Override
    public String restart(String service) {
        CommandResult result = runner.run("docker", "restart", service);
        return result.exitCode() == 0
                ? "Restarted " + service
                : "Restart failed for " + service + ": " + result.stdout();
    }

}
