package com.company.project.presentation;

import com.company.project.common.service.ResponseBuilderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final ResponseBuilderService responseBuilder;

    public HealthController(ResponseBuilderService responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        return responseBuilder.buildHealthResponse("UP");
    }
}
