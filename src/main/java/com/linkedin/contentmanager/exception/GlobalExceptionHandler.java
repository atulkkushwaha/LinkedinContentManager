package com.linkedin.contentmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central error handling for the pipeline API. Every failure mode called out
 * in the original Python script (missing env vars, failed API calls) is
 * translated into a clear HTTP status + JSON body instead of a stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingApiKeyException.class)
    public ResponseEntity<Map<String, Object>> handleMissingKey(MissingApiKeyException ex) {
        log.warn("Request rejected because an API key is missing message={}", ex.getMessage());
        return build(HttpStatus.PRECONDITION_FAILED, "MISSING_API_KEY", ex.getMessage());
    }

    @ExceptionHandler(UpstreamApiException.class)
    public ResponseEntity<Map<String, Object>> handleUpstream(UpstreamApiException ex) {
        log.warn("Upstream API request failed message={}", ex.getMessage());
        return build(HttpStatus.BAD_GATEWAY, "UPSTREAM_API_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .orElse("Invalid request");
            log.info("Request validation failed detail={}", detail);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled pipeline failure exception={}", ex.getClass().getSimpleName(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Pipeline execution failed: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", code);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
