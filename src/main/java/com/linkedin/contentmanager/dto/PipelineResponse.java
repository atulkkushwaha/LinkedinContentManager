package com.linkedin.contentmanager.dto;

import java.util.List;

/** Full result of running the 5-stage pipeline for a given topic. */
public class PipelineResponse {

    private String topic;
    private List<StageResult> stages;
    private String finalPublishingBrief;

    public PipelineResponse() {
    }

    public PipelineResponse(String topic, List<StageResult> stages, String finalPublishingBrief) {
        this.topic = topic;
        this.stages = stages;
        this.finalPublishingBrief = finalPublishingBrief;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<StageResult> getStages() {
        return stages;
    }

    public void setStages(List<StageResult> stages) {
        this.stages = stages;
    }

    public String getFinalPublishingBrief() {
        return finalPublishingBrief;
    }

    public void setFinalPublishingBrief(String finalPublishingBrief) {
        this.finalPublishingBrief = finalPublishingBrief;
    }
}
