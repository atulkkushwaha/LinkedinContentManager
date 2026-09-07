package com.linkedin.contentmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Autonomous LinkedIn Content Manager.
 *
 * Java/Spring Boot port of the original Python + CrewAI pipeline. Five
 * "agents" (plain service classes with a role/goal/backstory-driven system
 * prompt) run in a strict sequential order, each one's output becoming the
 * next one's input - mirroring CrewAI's Process.sequential + memory=True
 * behaviour:
 *
 *   Research -> Write -> Critique -> Optimize -> Schedule
 */
@SpringBootApplication
public class ContentManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentManagerApplication.class, args);
    }
}
