package com.senevilabs.supportagent.ops;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "support.agent")
public class ServiceCatalog {
    private List<String> dockerContainers = List.of();

    public List<String> getDockerContainers() {
        return dockerContainers;
    }

    public void setDockerContainers(List<String> dockerContainers) {
        this.dockerContainers = dockerContainers;
    }

    public boolean allowsDockerContainer(String name) {
        return dockerContainers.contains(name);
    }

    public String describeDockerContainers() {
        return dockerContainers.isEmpty() ? "(none configured)" : String.join(", ", dockerContainers);
    }

}
