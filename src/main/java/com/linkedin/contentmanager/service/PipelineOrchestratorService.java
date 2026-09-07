package com.linkedin.contentmanager.service;

import com.linkedin.contentmanager.agent.*;
import com.linkedin.contentmanager.dto.PipelineResponse;
import com.linkedin.contentmanager.dto.StageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs the 5-agent pipeline in strict sequential order:
 *   Research -> Write -> Critique -> Optimize -> Schedule
 *
 * This is the Java equivalent of CrewAI's Crew(process=Process.sequential,
 * memory=True): each stage's output is explicitly threaded into the next
 * stage's input, so nothing is inferred implicitly - it's just plain method
 * calls instead of a framework's task/context graph.
 */
@Service
public class PipelineOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(PipelineOrchestratorService.class);

    private final TrendResearcherAgent trendResearcher;
    private final ContentWriterAgent contentWriter;
    private final ContentCriticAgent contentCritic;
    private final ContentOptimizerAgent contentOptimizer;
    private final SchedulingAgent schedulingAgent;

    public PipelineOrchestratorService(TrendResearcherAgent trendResearcher,
                                        ContentWriterAgent contentWriter,
                                        ContentCriticAgent contentCritic,
                                        ContentOptimizerAgent contentOptimizer,
                                        SchedulingAgent schedulingAgent) {
        this.trendResearcher = trendResearcher;
        this.contentWriter = contentWriter;
        this.contentCritic = contentCritic;
        this.contentOptimizer = contentOptimizer;
        this.schedulingAgent = schedulingAgent;
    }

    public PipelineResponse run(String topic) {
        List<StageResult> stages = new ArrayList<>();

        // Stage 1 - Research
        long stageStartedAt = System.nanoTime();
        log.info("Pipeline stage started stage=Research");
        String researchBrief = trendResearcher.research(topic);
        stages.add(new StageResult(1, "Research", trendResearcher.getRole(), researchBrief));
        log.info("Pipeline stage completed stage=Research outputLength={} durationMs={}",
            researchBrief.length(), elapsedMs(stageStartedAt));

        // Stage 2 - Write (consumes research brief)
        stageStartedAt = System.nanoTime();
        log.info("Pipeline stage started stage=Writing");
        String draftPost = contentWriter.write(topic, researchBrief);
        stages.add(new StageResult(2, "Writing", contentWriter.getRole(), draftPost));
        log.info("Pipeline stage completed stage=Writing outputLength={} durationMs={}",
            draftPost.length(), elapsedMs(stageStartedAt));

        // Stage 3 - Critique (consumes draft)
        stageStartedAt = System.nanoTime();
        log.info("Pipeline stage started stage=Critique");
        String critique = contentCritic.critique(draftPost);
        stages.add(new StageResult(3, "Critique", contentCritic.getRole(), critique));
        log.info("Pipeline stage completed stage=Critique outputLength={} durationMs={}",
            critique.length(), elapsedMs(stageStartedAt));

        // Stage 4 - Optimize (consumes draft + critique)
        stageStartedAt = System.nanoTime();
        log.info("Pipeline stage started stage=Optimization");
        String optimizedPost = contentOptimizer.optimize(draftPost, critique);
        stages.add(new StageResult(4, "Optimization", contentOptimizer.getRole(), optimizedPost));
        log.info("Pipeline stage completed stage=Optimization outputLength={} durationMs={}",
            optimizedPost.length(), elapsedMs(stageStartedAt));

        // Stage 5 - Schedule (consumes optimized post)
        stageStartedAt = System.nanoTime();
        log.info("Pipeline stage started stage=Scheduling");
        String publishingBrief = schedulingAgent.schedule(topic, optimizedPost);
        stages.add(new StageResult(5, "Scheduling", schedulingAgent.getRole(), publishingBrief));
        log.info("Pipeline stage completed stage=Scheduling outputLength={} durationMs={}",
            publishingBrief.length(), elapsedMs(stageStartedAt));

        return new PipelineResponse(topic, stages, publishingBrief);
    }

        private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
        }
}
