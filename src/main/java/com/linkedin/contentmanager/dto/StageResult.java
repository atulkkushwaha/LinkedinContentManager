package com.linkedin.contentmanager.dto;

/**
 * Output of a single agent/stage in the pipeline. `stageNumber` and `stageName`
 * let the React UI render a step-by-step timeline as each stage completes.
 */
public class StageResult {

    private int stageNumber;
    private String stageName;
    private String agentRole;
    private String output;

    public StageResult() {
    }

    public StageResult(int stageNumber, String stageName, String agentRole, String output) {
        this.stageNumber = stageNumber;
        this.stageName = stageName;
        this.agentRole = agentRole;
        this.output = output;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getAgentRole() {
        return agentRole;
    }

    public void setAgentRole(String agentRole) {
        this.agentRole = agentRole;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
