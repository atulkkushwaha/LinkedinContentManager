package com.linkedin.contentmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request body for POST /api/pipeline/generate - the LinkedIn niche/topic. */
public class GenerateRequest {

    @NotBlank(message = "must not be blank - e.g. \"Agentic AI\"")
    @Size(max = 200, message = "must be 200 characters or fewer")
    private String topic;

    public GenerateRequest() {
    }

    public GenerateRequest(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
