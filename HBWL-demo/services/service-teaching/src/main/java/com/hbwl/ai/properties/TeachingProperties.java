package com.hbwl.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "llm")
@Data
public class TeachingProperties {

    private String baseURL;

    private String apiKey;

    private String modelName;

    private String streamBaseURL;

    private String streamModelName;

    private String embedBaseURL;

    private String embedModelName;
}
