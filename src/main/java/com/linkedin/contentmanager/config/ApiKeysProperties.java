package com.linkedin.contentmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds to `app.openai-api-key` / `app.serper-api-key` in application.yml,
 * which in turn resolve from the OPENAI_API_KEY / SERPER_API_KEY environment
 * variables (see application.yml). This is the Java equivalent of the
 * Python script's `load_dotenv()` + `os.getenv(...)` calls.
 */
@Component
@ConfigurationProperties(prefix = "app")
public class ApiKeysProperties {

    private String openaiApiKey;
    private String serperApiKey;
    private String openaiModel = "gpt-4o";
    private String omniRouteBaseUrl = "http://localhost:20128/v1";
    private String omniRouteModel = "gpt-4o";
    private String omniRouteApiKey = "";

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }

    public void setOpenaiApiKey(String openaiApiKey) {
        this.openaiApiKey = openaiApiKey;
    }

    public String getSerperApiKey() {
        return serperApiKey;
    }

    public void setSerperApiKey(String serperApiKey) {
        this.serperApiKey = serperApiKey;
    }

    public String getOpenaiModel() {
        return openaiModel;
    }

    public void setOpenaiModel(String openaiModel) {
        this.openaiModel = openaiModel;
    }

    public String getOmniRouteBaseUrl() {
        return omniRouteBaseUrl;
    }

    public void setOmniRouteBaseUrl(String omniRouteBaseUrl) {
        this.omniRouteBaseUrl = omniRouteBaseUrl;
    }

    public String getOmniRouteModel() {
        return omniRouteModel;
    }

    public void setOmniRouteModel(String omniRouteModel) {
        this.omniRouteModel = omniRouteModel;
    }

    public String getOmniRouteApiKey() {
        return omniRouteApiKey;
    }

    public void setOmniRouteApiKey(String omniRouteApiKey) {
        this.omniRouteApiKey = omniRouteApiKey;
    }

    public boolean isOpenaiConfigured() {
        return openaiApiKey != null && !openaiApiKey.isBlank();
    }

    public boolean isSerperConfigured() {
        return serperApiKey != null && !serperApiKey.isBlank();
    }
}
