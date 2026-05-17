package com.hbwl.ai.service;

import com.hbwl.pojo.ai.PromptInfo;

import java.util.List;

//职责：读取“当前发布版本”提示词快照, 提示词持久化
public interface PromptService {

    //提示词持久化
    int addPrompt(PromptInfo promptInfo, String prompt);

    int deletePrompt(Integer id);

    int updatePrompt(PromptInfo promptInfo, String prompt);

    List<PromptInfo> getPromptInfos(Integer id, String creatorId);

    //获取提示词
    String getPrompt(Integer id);

    //管理agent读取的提示词版本
    boolean setRuntimePrompt(Integer id);

    String getRuntimePrompt();
}
