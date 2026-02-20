package com.company.project.infrastructure;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Slf4j
@Configuration
public class ApplicationLifecycleConfig {

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application is ready to serve requests");
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Application context refreshed successfully");
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Graceful shutdown initiated...");
        log.info("Closing database connections, cache, and other resources...");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Application bean destruction completed");
    }
}
