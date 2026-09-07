package com.linkedin.contentmanager.exception;

/** Thrown at startup-of-request time when a required API key is not configured. */
public class MissingApiKeyException extends RuntimeException {
    public MissingApiKeyException(String message) {
        super(message);
    }
}
