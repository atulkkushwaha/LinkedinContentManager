package com.linkedin.contentmanager.agent;

import com.linkedin.contentmanager.client.OmniRouteClient;
import org.springframework.stereotype.Component;

/** Stage 2 - LinkedIn Content Writer. LLM-only, no external tools. */
@Component
public class ContentWriterAgent extends Agent {

    public ContentWriterAgent(OmniRouteClient omniRouteClient) {
        super(
                "LinkedIn Content Writer",
                "Write engaging, high-quality LinkedIn posts based on research provided",
                "Seasoned LinkedIn ghostwriter for industry leaders; expert in the LinkedIn " +
                        "algorithm, hook writing, storytelling, CTA placement, and a conversational " +
                        "professional tone",
                omniRouteClient
        );
    }

    public String write(String topic, String researchBrief) {
        String userPrompt = """
                Using the research brief below, write a compelling LinkedIn post about %s.
                Include a strong hook (first 2 lines), a storytelling or value-driven
                body, a clear CTA, and 150-300 words total. Use trending angles and
                hooks from the research.

                Research brief:
                %s

                Return the complete LinkedIn post draft with hook, body, CTA, and
                suggested hashtags.
                """.formatted(topic, researchBrief);

        return run(userPrompt);
    }

    @Override
    public String run(String userTaskPrompt) {
        return completeTask(userTaskPrompt);
    }
}
