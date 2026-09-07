package com.linkedin.contentmanager.agent;

import com.linkedin.contentmanager.client.OmniRouteClient;
import org.springframework.stereotype.Component;

/** Stage 4 - LinkedIn Post Optimizer. LLM-only, no external tools. */
@Component
public class ContentOptimizerAgent extends Agent {

    public ContentOptimizerAgent(OmniRouteClient omniRouteClient) {
        super(
                "LinkedIn Post Optimizer",
                "Rewrite posts incorporating critic feedback to maximize LinkedIn engagement",
                "LinkedIn growth expert and copywriter; master of formatting (short lines, " +
                        "strategic breaks, emoji usage, hashtag optimization, hook patterns, mobile readability)",
                omniRouteClient
        );
    }

    public String optimize(String originalDraft, String critique) {
        String userPrompt = """
                Take the original LinkedIn post and the critic's feedback below.
                Rewrite it incorporating all feedback: improve the hook, tighten the
                copy, optimize formatting (short lines, line breaks, strategic emoji),
                strengthen the CTA, and optimize hashtags.

                Original post:
                %s

                Critic feedback:
                %s

                Produce the final, polished, publish-ready version.
                """.formatted(originalDraft, critique);

        return run(userPrompt);
    }

    @Override
    public String run(String userTaskPrompt) {
        return completeTask(userTaskPrompt);
    }
}
