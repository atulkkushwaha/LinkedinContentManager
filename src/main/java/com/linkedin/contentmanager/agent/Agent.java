package com.linkedin.contentmanager.agent;

import com.linkedin.contentmanager.client.OmniRouteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for every pipeline agent. Mirrors CrewAI's Agent(role, goal,
 * backstory): the three fields are combined into a system prompt that frames
 * every call to the LLM, so each agent "stays in character" for its one job.
 */
public abstract class Agent {

    private static final Logger log = LoggerFactory.getLogger(Agent.class);

    private final String role;
    private final String goal;
    private final String backstory;
    protected final OmniRouteClient omniRouteClient;

    protected Agent(String role, String goal, String backstory, OmniRouteClient omniRouteClient) {
        this.role = role;
        this.goal = goal;
        this.backstory = backstory;
        this.omniRouteClient = omniRouteClient;
    }

    public String getRole() {
        return role;
    }

    /** Builds the system prompt from role/goal/backstory, as CrewAI does internally. */
    protected String systemPrompt() {
        return """
                You are the %s.
                Your goal: %s
                Backstory: %s

                Stay strictly within this role. Respond only with the deliverable
                requested in the task - no meta-commentary about being an AI.
                """.formatted(role, goal, backstory);
    }

    protected String completeTask(String userTaskPrompt) {
        long startedAt = System.nanoTime();
        log.info("Agent task started agent={} promptLength={}", role, userTaskPrompt.length());
        try {
            String output = omniRouteClient.complete(systemPrompt(), userTaskPrompt);
            log.info("Agent task completed agent={} outputLength={} durationMs={}",
                    role, output.length(), elapsedMs(startedAt));
            return output;
        } catch (RuntimeException e) {
            log.warn("Agent task failed agent={} durationMs={} exception={}",
                    role, elapsedMs(startedAt), e.getClass().getSimpleName());
            throw e;
        }
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    /** Runs this agent's task and returns its raw text output. */
    public abstract String run(String userTaskPrompt);
}
