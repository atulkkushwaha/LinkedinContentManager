package com.linkedin.contentmanager.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * Fetches and extracts the readable text of a web page - the equivalent of
 * CrewAI's ScrapeWebsiteTool. Kept separate from SerperSearchClient so the
 * Trend Researcher agent can optionally deep-dive into a specific URL
 * surfaced by search.
 */
@Component
public class WebScraperClient {

    private static final int TIMEOUT_MS = 8000;
    private static final int MAX_CHARS = 4000;

    /** Returns the visible text of a page, truncated to keep prompts small. Never throws. */
    public String scrape(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .userAgent("Mozilla/5.0 (compatible; LinkedInContentManagerBot/1.0)")
                    .get();
            String text = doc.body().text();
            return text.length() > MAX_CHARS ? text.substring(0, MAX_CHARS) + "..." : text;
        } catch (Exception e) {
            // A failed scrape shouldn't fail the whole pipeline - the researcher
            // agent can still work from search snippets alone.
            return "(Could not scrape " + url + ": " + e.getMessage() + ")";
        }
    }
}
