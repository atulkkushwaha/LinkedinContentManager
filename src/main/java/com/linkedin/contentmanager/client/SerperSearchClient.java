package com.linkedin.contentmanager.client;

import com.linkedin.contentmanager.config.ApiKeysProperties;
import com.linkedin.contentmanager.exception.MissingApiKeyException;
import com.linkedin.contentmanager.exception.UpstreamApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wraps the Serper.dev Google-search API - the direct equivalent of CrewAI's
 * SerperDevTool. Used only by the Trend Researcher agent.
 */
@Component
public class SerperSearchClient {

    private static final String SEARCH_URL = "https://google.serper.dev/search";
    private static final Logger log = LoggerFactory.getLogger(SerperSearchClient.class);

    private final WebClient webClient;
    private final ApiKeysProperties apiKeys;

    public SerperSearchClient(WebClient webClient, ApiKeysProperties apiKeys) {
        this.webClient = webClient;
        this.apiKeys = apiKeys;
    }

    /** Runs a web search and returns a compact, LLM-friendly text summary of the top results. */
    @SuppressWarnings("unchecked")
    public String search(String query) {
        if (!apiKeys.isSerperConfigured()) {
            log.info("Serper search skipped because API key is not configured queryLength={}", query.length());
            // Graceful degradation: return a stub so the pipeline can still run without a Serper key
            return "No live search results available (SERPER_API_KEY not configured). " +
                   "Proceeding with LLM knowledge for query: " + query;
        }

        long startedAt = System.nanoTime();
        log.debug("Serper search started queryLength={}", query.length());
        try {
            Map<String, Object> response = webClient.post()
                    .uri(SEARCH_URL)
                    .header("X-API-KEY", apiKeys.getSerperApiKey())
                    .bodyValue(Map.of("q", query))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("organic")) {
                log.info("Serper search completed with no results durationMs={}", elapsedMs(startedAt));
                return "No search results found for: " + query;
            }

            List<Map<String, Object>> organic = (List<Map<String, Object>>) response.get("organic");
                String result = organic.stream()
                    .limit(8)
                    .map(r -> "- " + r.getOrDefault("title", "") + ": "
                            + r.getOrDefault("snippet", "") + " (" + r.getOrDefault("link", "") + ")")
                    .collect(Collectors.joining("\n"));
                    log.debug("Serper search completed resultCount={} durationMs={}", organic.size(), elapsedMs(startedAt));
                    return result;
        } catch (MissingApiKeyException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Serper search failed durationMs={} exception={}", elapsedMs(startedAt), e.getClass().getSimpleName());
            throw new UpstreamApiException("Serper search failed: " + e.getMessage(), e);
        }
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
