package com.linkedin.contentmanager.agent;

import com.linkedin.contentmanager.client.OmniRouteClient;
import org.springframework.stereotype.Component;

/** Stage 3 - Content Quality Critic. LLM-only, no external tools. */
@Component
public class ContentCriticAgent extends Agent {

    public ContentCriticAgent(OmniRouteClient omniRouteClient) {
        super(
                "Content Quality Critic",
                "Review LinkedIn posts and provide detailed constructive feedback on " +
                        "engagement potential, tone, structure, clarity, hook strength, and CTA effectiveness",
                "Harsh but fair editor with thousands of LinkedIn post reviews; distinguishes " +
                        "posts that get 10 likes from those with 10k+ impressions; delivers specific, " +
                        "actionable feedback",
                omniRouteClient
        );
    }

    public String critique(String draftPost) {
        String userPrompt = """
                Critically review the LinkedIn post draft below. Evaluate:
                  - Hook strength (will people click "see more"?)
                  - Storytelling quality
                  - Engagement potential
                  - CTA effectiveness
                  - Tone consistency
                  - LinkedIn formatting
                  - Viral potential

                Post draft:
                %s

                Provide a score out of 10, strengths, weaknesses, and specific
                actionable improvement suggestions.
                """.formatted(draftPost);

        return run(userPrompt);
    }

    @Override
    public String run(String userTaskPrompt) {
        return completeTask(userTaskPrompt);
    }
}
