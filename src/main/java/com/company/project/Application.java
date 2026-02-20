package com.company.project;

import com.company.project.application.McpToolsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        log.info("Starting application...");
        SpringApplication.run(Application.class, args);
        log.info("Application started successfully");
    }

    @Bean
    public ToolCallbackProvider toolCallbackProvider(McpToolsService mcpToolsService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpToolsService)
                .build();
    }
}
