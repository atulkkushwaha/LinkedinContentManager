package com.linkedin.contentmanager.agent;

import com.linkedin.contentmanager.client.OmniRouteClient;
import com.linkedin.contentmanager.client.SerperSearchClient;
import com.linkedin.contentmanager.client.WebScraperClient;
import org.springframework.stereotype.Component;

/**
 * Stage 1 - LinkedIn Trend Researcher.
 * Tools: SerperDevTool + ScrapeWebsiteTool (ported 1:1 from the Python agent).
 */
@Component
public class TrendResearcherAgent extends Agent {

    private final SerperSearchClient searchClient;
    private final WebScraperClient scraperClient;

    public TrendResearcherAgent(OmniRouteClient omniRouteClient,
                                 SerperSearchClient searchClient,
                                 WebScraperClient scraperClient) {
        super(
                "LinkedIn Trend Researcher",
                "Research latest trending topics, hashtags, and content themes for a given niche",
                "Expert social media researcher who monitors LinkedIn trends, viral posts, and " +
                        "industry news; knows what drives engagement on LinkedIn",
                omniRouteClient
        );
        this.searchClient = searchClient;
        this.scraperClient = scraperClient;
    }

    public String research(String topic) {
        // Tool call 1: broad search for current trends on the topic.
        String searchResults = searchClient.search("LinkedIn trending topics hashtags " + topic + " 2026");

        // Tool call 2: scrape the top hit for extra depth, mirroring how a
        // CrewAI ReAct-style agent would decide to follow up a search with a scrape.
        String topUrl = extractFirstUrl(searchResults);
        String scraped = topUrl != null ? scraperClient.scrape(topUrl) : "";

        String userPrompt = """
                Research the latest trends, viral content patterns, and hot topics on
                LinkedIn for the niche: %s.
                Identify 3-5 trending angles, relevant hashtags, and content hooks
                currently performing well.

                Search results:
                %s

                Additional page content:
                %s

                Deliver a structured research brief with: trending topics, suggested
                angles, top-performing hashtags, and content hook ideas.
                """.formatted(topic, searchResults, scraped);

        return run(userPrompt);
    }

    @Override
    public String run(String userTaskPrompt) {
        return completeTask(userTaskPrompt);
    }

    private String extractFirstUrl(String searchResults) {
        int start = searchResults.indexOf("http");
        if (start == -1) return null;
        int end = searchResults.indexOf(')', start);
        return end == -1 ? searchResults.substring(start) : searchResults.substring(start, end);
    }
}
