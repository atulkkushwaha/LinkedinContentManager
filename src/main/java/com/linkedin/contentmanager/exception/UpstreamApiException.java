package com.linkedin.contentmanager.exception;

/** Wraps failures from calling OpenAI, Serper, or a scraped web page. */
public class UpstreamApiException extends RuntimeException {
    public UpstreamApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpstreamApiException(String message) {
        super(message);
    }
}
