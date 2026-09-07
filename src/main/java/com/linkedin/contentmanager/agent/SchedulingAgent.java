package com.linkedin.contentmanager.agent;

import com.linkedin.contentmanager.client.OmniRouteClient;
import org.springframework.stereotype.Component;

/** Stage 5 - LinkedIn Publishing Strategist. LLM-only, no external tools. */
@Component
public class SchedulingAgent extends Agent {

    public SchedulingAgent(OmniRouteClient omniRouteClient) {
        super(
                "LinkedIn Publishing Strategist",
                "Determine optimal posting time, finalize formatting with hashtags, and create " +
                        "publishing-ready output with scheduling recommendations",
                "LinkedIn analytics expert; understands optimal posting times by industry, " +
                        "audience timezone, and day of week",
                omniRouteClient
        );
    }

    public String schedule(String topic, String optimizedPost) {
        String userPrompt = """
                Analyze the final post content and target audience for %s.
                Recommend the best day and time to publish (with timezone), provide
                the final formatted post ready for LinkedIn copy-paste, and include a
                brief with hashtag strategy and first-hour engagement tips.

                Final optimized post:
                %s

                Return a complete publishing brief with:
                  1. Recommended posting day and time with timezone
                  2. The final formatted post ready to copy-paste
                  3. A hashtag strategy (primary + secondary tags)
                  4. 3-5 first-hour engagement tips
                """.formatted(topic, optimizedPost);

        return run(userPrompt);
    }

    @Override
    public String run(String userTaskPrompt) {
        return completeTask(userTaskPrompt);
    }
}
