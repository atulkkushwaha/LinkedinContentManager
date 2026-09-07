package com.linkedin.contentmanager.client;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.linkedin.contentmanager.config.ApiKeysProperties;
import com.linkedin.contentmanager.exception.UpstreamApiException;

/**
 * Wrapper around the local OmniRoute API (OpenAI-compatible endpoint).
 */
@Component
public class OmniRouteClient {

    private static final Logger log = LoggerFactory.getLogger(OmniRouteClient.class);

    private final WebClient webClient;
    private final ApiKeysProperties apiKeys;

    public OmniRouteClient(WebClient webClient, ApiKeysProperties apiKeys) {
        this.webClient = webClient;
        this.apiKeys = apiKeys;
    }

    @SuppressWarnings("unchecked")
    public String complete(String systemPrompt, String userPrompt) {
        String url = apiKeys.getOmniRouteBaseUrl() + "/chat/completions";
        String model = apiKeys.getOmniRouteModel();
        String apiKey = apiKeys.getOmniRouteApiKey();
        long startedAt = System.nanoTime();
        log.debug("OmniRoute request started model={} systemPromptLength={} userPromptLength={}",
            model, systemPrompt.length(), userPrompt.length());

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.7,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        var requestSpec = webClient.post()
                .uri(url)
                .bodyValue(body);

        // Attach Bearer token if an API key is configured
        if (apiKey != null && !apiKey.isBlank()) {
            requestSpec = webClient.post()
                    .uri(url)
                    .header("Authorization", bearerToken(apiKey))
                    .bodyValue(body);
        }

        try {
            String content = requestSpec
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return (String) message.get("content");
                    })
                    .blockOptional()
                    .orElseThrow(() -> new UpstreamApiException("Empty response from OmniRoute API"));
            log.debug("OmniRoute request completed model={} responseLength={} durationMs={}",
                    model, content.length(), elapsedMs(startedAt));
            return content;
        } catch (UpstreamApiException e) {
            log.warn("OmniRoute request failed model={} durationMs={} reason={}",
                    model, elapsedMs(startedAt), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("OmniRoute request failed model={} durationMs={} exception={}",
                    model, elapsedMs(startedAt), e.getClass().getSimpleName());
            throw new UpstreamApiException("OmniRoute request failed: " + e.getMessage(), e);
        }
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private String bearerToken(String apiKey) {
        return apiKey.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())
                ? apiKey
                : "Bearer " + apiKey;
    }
}
