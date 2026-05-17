package com.hbwl.ai.utils;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PromptBaseTemplateLoader {

    @Value("classpath:prompt/base-prompt.txt")
    private Resource basePromptResource;

    @Getter
    private String basePrompt;

    @Value("classpath:prompt/default-prompt.txt")
    private Resource defaultPromptResource;

    @Getter
    private String defaultPrompt;

    @PostConstruct
    public void init() throws IOException {
        byte[] bytes = basePromptResource.getInputStream().readAllBytes();
        this.basePrompt = new String(bytes, StandardCharsets.UTF_8).trim();
        bytes = defaultPromptResource.getInputStream().readAllBytes();
        this.defaultPrompt = new String(bytes, StandardCharsets.UTF_8).trim();
    }
}
