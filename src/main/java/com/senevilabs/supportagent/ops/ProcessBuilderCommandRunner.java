package com.senevilabs.supportagent.ops;

import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ProcessBuilderCommandRunner implements CommandRunner {
    @Override
    public CommandResult run(String... command) {
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            String output = new String(process.getInputStream().readAllBytes());
            return new CommandResult(process.waitFor(), output.trim());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Command failed: " + String.join(" ", command), e);
        }
    }
}
