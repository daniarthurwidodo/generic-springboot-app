package com.company.project.application;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class McpToolsService {

    @Tool(description = "Get a greeting message for the given name")
    public String greet(String name) {
        return "Hello, " + (name != null && !name.isBlank() ? name : "World") + "!";
    }

    @Tool(description = "Get the current application health status")
    public String getHealthStatus() {
        return "Application is healthy and running";
    }
}
