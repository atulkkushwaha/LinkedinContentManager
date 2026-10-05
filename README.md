# LinkedIn Content Manager

A Spring Boot application that generates LinkedIn content through a five-stage sequential AI pipeline:

1. Research
2. Write
3. Critique
4. Optimize
5. Schedule / publishing brief

The API exposes a single generation endpoint that accepts a topic and returns the full pipeline result, including each stage’s agent output and the final publishing brief.

---

## Overview

This project is designed to mirror a multi-agent content workflow where each stage depends on the previous one:

- The `TrendResearcherAgent` researches trending topics and hooks for the requested niche.
- The `ContentWriterAgent` drafts a LinkedIn post based on the research brief.
- The `ContentCriticAgent` reviews the draft and provides actionable feedback.
- The `ContentOptimizerAgent` rewrites the content to improve structure, readability, and engagement.
- The `SchedulingAgent` creates the final publishing brief with recommendations and copy-ready content.

The orchestrator is implemented in `PipelineOrchestratorService`, which runs these stages in strict order and returns the combined output.

---

## Project Structure

- `controller/PipelineController` – REST entry point
- `service/PipelineOrchestratorService` – sequential pipeline runner
- `agent/*` – individual specialist agents
- `dto/*` – request/response models
- `exception/*` – validation and fallback error handling
- `config/*` – app configuration and CORS

---

## API Endpoints

### 1) Health Check

Method: `GET`

URL:

```http
GET /api/pipeline/health
```

Purpose:
- Verifies the service is running.

Example response:

```text
OK
```

HTTP status:
- `200 OK`

---

### 2) Generate LinkedIn Content

Method: `POST`

URL:

```http
POST /api/pipeline/generate
```

Purpose:
- Runs the full five-stage pipeline for a target topic.
- Returns the original topic, per-stage outputs, and final publishing brief.

Request content type:
- `application/json`

### Request Body

```json
{
  "topic": "Agentic AI"
}
```

Validation rules:
- `topic` is required
- `topic` cannot be blank
- maximum length is 200 characters

### Response Body

```json
{
  "topic": "Agentic AI",
  "stages": [
    {
      "stageNumber": 1,
      "stageName": "Research",
      "agentRole": "LinkedIn Trend Researcher",
      "output": "...research brief..."
    },
    {
      "stageNumber": 2,
      "stageName": "Writing",
      "agentRole": "LinkedIn Content Writer",
      "output": "...draft LinkedIn post..."
    },
    {
      "stageNumber": 3,
      "stageName": "Critique",
      "agentRole": "Content Quality Critic",
      "output": "...feedback and score..."
    },
    {
      "stageNumber": 4,
      "stageName": "Optimization",
      "agentRole": "LinkedIn Post Optimizer",
      "output": "...optimized post..."
    },
    {
      "stageNumber": 5,
      "stageName": "Scheduling",
      "agentRole": "LinkedIn Publishing Strategist",
      "output": "...final publishing brief..."
    }
  ],
  "finalPublishingBrief": "...final publishing brief..."
}
```

HTTP status:
- `200 OK`

---

## Request and Response Models

### GenerateRequest

```java
public class GenerateRequest {
    private String topic;
}
```

Example JSON:

```json
{
  "topic": "AI automation for founders"
}
```

### StageResult

```java
public class StageResult {
    private int stageNumber;
    private String stageName;
    private String agentRole;
    private String output;
}
```

### PipelineResponse

```java
public class PipelineResponse {
    private String topic;
    private List<StageResult> stages;
    private String finalPublishingBrief;
}
```

---

## Pipeline Stages

### 1) Research Stage

Agent: `TrendResearcherAgent`

Responsibilities:
- Searches for current LinkedIn trends using Serper
- Scrapes a likely result page for extra context
- Builds a structured research brief with:
  - trending topics
  - angle ideas
  - hashtags
  - hooks

Input:
- topic

Output:
- research brief string

### 2) Writing Stage

Agent: `ContentWriterAgent`

Responsibilities:
- Writes a LinkedIn post from the research brief
- Includes hook, value-driven body, CTA, and hashtags
- Targets 150–300 words

Input:
- topic + research brief

Output:
- draft LinkedIn post

### 3) Critique Stage

Agent: `ContentCriticAgent`

Responsibilities:
- Reviews the draft for hook strength, readability, CTA, engagement, and tone
- Returns a score out of 10 with suggestions for improvement

Input:
- draft post

Output:
- critique and improvement suggestions

### 4) Optimization Stage

Agent: `ContentOptimizerAgent`

Responsibilities:
- Rewrites the draft based on critique feedback
- Improves formatting, CTA, hook, readability, and hashtags
- Produces a publish-ready LinkedIn post

Input:
- original draft + critique

Output:
- optimized final draft

### 5) Scheduling Stage

Agent: `SchedulingAgent`

Responsibilities:
- Recommends posting day/time and timezone
- Produces a final copy-ready LinkedIn post
- Adds hashtag strategy and first-hour engagement recommendations

Input:
- topic + optimized post

Output:
- final publishing brief

---

## Example Request

Using `curl`:

```bash
curl -X POST "http://localhost:8080/api/pipeline/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "Agentic AI"
  }'
```

---

## Example Response

```json
{
  "topic": "Agentic AI",
  "stages": [
    {
      "stageNumber": 1,
      "stageName": "Research",
      "agentRole": "LinkedIn Trend Researcher",
      "output": "Trending angles: AI copilots for small teams, automation governance, ROI of agentic workflows..."
    },
    {
      "stageNumber": 2,
      "stageName": "Writing",
      "agentRole": "LinkedIn Content Writer",
      "output": "Most founders are not asking whether AI will change work..."
    },
    {
      "stageNumber": 3,
      "stageName": "Critique",
      "agentRole": "Content Quality Critic",
      "output": "Score: 8.5/10. Strong hook and practical insights. Could improve CTA and tighten opening lines."
    },
    {
      "stageNumber": 4,
      "stageName": "Optimization",
      "agentRole": "LinkedIn Post Optimizer",
      "output": "A clearer, punchier version of the original post with stronger structure and formatting."
    },
    {
      "stageNumber": 5,
      "stageName": "Scheduling",
      "agentRole": "LinkedIn Publishing Strategist",
      "output": "Best time: Tuesday, 9:30 AM PT. Primary hashtags: #AI #AgenticAI #Productivity ..."
    }
  ],
  "finalPublishingBrief": "Best time: Tuesday, 9:30 AM PT. Final post ready for copy-paste, plus hashtag strategy and engagement tips."
}
```

---

## Error Handling

The API returns structured JSON error responses for request validation and upstream API issues.

### Validation Error Example

Request:

```json
{
  "topic": " "
}
```

Response:

```json
{
  "timestamp": "2026-10-05T10:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "topic must not be blank - e.g. \"Agentic AI\""
}
```

### Missing API Key Example

Response:

```json
{
  "timestamp": "2026-10-05T10:00:00Z",
  "status": 412,
  "error": "MISSING_API_KEY",
  "message": "Missing required API key: OPENAI_API_KEY or OMNIROUTE_API_KEY"
}
```

### Upstream API Failure Example

Response:

```json
{
  "timestamp": "2026-10-05T10:00:00Z",
  "status": 502,
  "error": "UPSTREAM_API_ERROR",
  "message": "Upstream provider request failed. Please check external API connectivity."
}
```

---

## Configuration and Environment Variables

The application reads secrets from environment variables defined in `application.yml`.

Required / optional environment variables:

```bash
export OPENAI_API_KEY="your-openai-key"
export SERPER_API_KEY="your-serper-key"
export OPENAI_MODEL_NAME="gpt-4o"
export OMNIROUTE_BASE_URL="http://localhost:20128/v1"
export OMNIROUTE_MODEL="gpt-4o"
export OMNIROUTE_API_KEY="your-omniroute-key"
```

Default app settings:

```yaml
server:
  port: 8080

app:
  openai-api-key: ${OPENAI_API_KEY:}
  serper-api-key: ${SERPER_API_KEY:}
  openai-model: ${OPENAI_MODEL_NAME:gpt-4o}
  omniroute-base-url: ${OMNIROUTE_BASE_URL:http://localhost:20128/v1}
  omniroute-model: ${OMNIROUTE_MODEL:gpt-4o}
  omniroute-api-key: ${OMNIROUTE_API_KEY:}
```

---

## Run the Application

### Prerequisites

- Java 17+
- Maven
- API credentials for the external content generation/search providers

### Build

```bash
mvn clean package
```

### Run locally

```bash
mvn spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

---

## Notes

- The service uses a synchronous pipeline. Each stage completes before the next begins.
- The response includes all agent outputs for transparency and debugging.
- CORS is enabled for local frontend development on `http://localhost:*`.
- Logs are written to `logs/content-manager.log`.

---

## Summary

This backend provides a complete LinkedIn content generation workflow with a single API endpoint, a strict five-stage agent pipeline, structured JSON outputs, and support for external research and LLM-powered content generation.
