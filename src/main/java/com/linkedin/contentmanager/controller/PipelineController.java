package com.linkedin.contentmanager.controller;

import com.linkedin.contentmanager.dto.GenerateRequest;
import com.linkedin.contentmanager.dto.PipelineResponse;
import com.linkedin.contentmanager.service.PipelineOrchestratorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * HTTP entry point for the LinkedIn Content Manager pipeline.
 *
 * POST /api/pipeline/generate  { "topic": "Agentic AI" }
 *   -> runs all 5 stages synchronously and returns every stage's output
 *      plus the final publishing brief.
 */
@RestController
@RequestMapping("/api/pipeline")
public class PipelineController {

    private static final Logger log = LoggerFactory.getLogger(PipelineController.class);

    private final PipelineOrchestratorService orchestrator;

    public PipelineController(PipelineOrchestratorService orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/generate")
    public ResponseEntity<PipelineResponse> generate(@Valid @RequestBody GenerateRequest request) {
        String topic = request.getTopic().trim();
        long startedAt = System.nanoTime();
        log.info("Pipeline request started topicLength={}", topic.length());
        PipelineResponse response = orchestrator.run(topic);
        log.info("Pipeline request completed topicLength={} stageCount={} durationMs={}",
                topic.length(), response.getStages().size(), elapsedMs(startedAt));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
