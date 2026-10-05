package com.senevilabs.supportagent.resolution;

import com.senevilabs.supportagent.knowledge.KnowledgeBaseService;
import com.senevilabs.supportagent.ops.ServiceCatalog;
import com.senevilabs.supportagent.ops.ServiceOperator;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class SupportTools {

    private final ServiceOperator operator;
    private final ServiceCatalog catalog;
    private final KnowledgeBaseService kb;

    public SupportTools(ServiceOperator operator, ServiceCatalog catalog, KnowledgeBaseService kb) {
        this.operator = operator;
        this.catalog = catalog;
        this.kb = kb;
    }

    @Tool(description = "Check the availability/health status of a Docker container service.")
    public String checkHealth(
            @ToolParam(description = "The exact service name to check") String service) {
        if (!catalog.allowsDockerContainer(service)) {
            return unknownService(service);
        }
        return operator.check(service);
    }

    @Tool(description = "Restart a Docker container service that is unhealthy.")
    public String restartService(
            @ToolParam(description = "The exact service name to restart") String service) {
        if (!catalog.allowsDockerContainer(service)) {
            return unknownService(service);
        }
        return operator.restart(service);
    }

    @Tool(description = "Search the internal knowledge base for documentation about an issue or service.")
    public String searchKnowledgeBase(
            @ToolParam(description = "What to look up in the knowledge base") String query) {
        return kb.search(query).stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
    }

    private String unknownService(String service) {
        return "Unknown service '" + service + "'. Available services: "
                + catalog.describeDockerContainers();
    }
}