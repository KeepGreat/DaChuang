package com.hbwl.ai.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RuntimePromptManager {

    private static final int DEFAULT_PROMPT_ID = 0;

    private final ConcurrentMap<Integer, String> promptCache = new ConcurrentHashMap<>();
    private final AtomicReference<PromptState> state = new AtomicReference<>();

    @Autowired
    private PromptStorageUtil promptStorageUtil;

    @Autowired
    private PromptBaseTemplateLoader promptBaseTemplateLoader;

    @PostConstruct
    public void init() {
        String defaultPrompt = promptBaseTemplateLoader.getDefaultPrompt();
        promptCache.put(DEFAULT_PROMPT_ID, defaultPrompt);
        state.set(new PromptState(DEFAULT_PROMPT_ID, defaultPrompt));
    }

    public boolean setRuntimePromptId(Integer id) {
        if (id == null || id < 0) {
            return false;
        }
        try {
            String prompt = getOrLoadPrompt(id);
            // 单次原子替换，保证 id 与 prompt 始终一致
            state.set(new PromptState(id, prompt));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public Integer getRuntimePromptId() {
        PromptState current = state.get();
        return current == null ? DEFAULT_PROMPT_ID : current.id();
    }

    public String getRuntimePrompt() {
        PromptState current = state.get();
        if (current != null && current.prompt() != null) {
            return current.prompt();
        }
        // 极端情况下兜底重建状态
        String defaultPrompt = getOrLoadPrompt(DEFAULT_PROMPT_ID);
        state.set(new PromptState(DEFAULT_PROMPT_ID, defaultPrompt));
        return defaultPrompt;
    }

    public String composePrompt(String basePrompt, String runtimePrompt) {
        if (basePrompt == null || basePrompt.isBlank()) {
            basePrompt = promptBaseTemplateLoader.getBasePrompt();
        }
        if (runtimePrompt == null || runtimePrompt.isBlank()) {
            runtimePrompt = getRuntimePrompt();
        }
        return basePrompt + "\n\n ###输出格式\n" + runtimePrompt;
    }

    // 刷新当前 id 对应的提示词（例如发布后文件内容变化）
    public boolean publish() {
        Integer id = getRuntimePromptId();
        evict(id);
        return setRuntimePromptId(id);
    }

    // 失效指定 id 的缓存；若是当前 id，同时刷新当前状态
    // 注意调用evict前需确保新txt文件已写入
    public void evict(Integer id) {
        if (id == null || id < 0) {
            return;
        }

        if (id == DEFAULT_PROMPT_ID) {
            promptCache.put(DEFAULT_PROMPT_ID, promptBaseTemplateLoader.getDefaultPrompt());
        } else {
            promptCache.remove(id);
        }

        PromptState current = state.get();
        if (current != null && current.id() == id) {
            String latest = getOrLoadPrompt(id);
            state.set(new PromptState(id, latest));
        }
    }

    // 失效全部缓存并回到默认提示词
    public void evictAll() {
        promptCache.clear();
        String defaultPrompt = promptBaseTemplateLoader.getDefaultPrompt();
        promptCache.put(DEFAULT_PROMPT_ID, defaultPrompt);
        state.set(new PromptState(DEFAULT_PROMPT_ID, defaultPrompt));
    }

    private String getOrLoadPrompt(Integer id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("id 不合法: " + id);
        }
        if (id == DEFAULT_PROMPT_ID) {
            return promptCache.computeIfAbsent(DEFAULT_PROMPT_ID, key -> promptBaseTemplateLoader.getDefaultPrompt());
        }
        return promptCache.computeIfAbsent(id, promptStorageUtil::loadPromptFromTxt);
    }

    private record PromptState(int id, String prompt) {
    }
}
