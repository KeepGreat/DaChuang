package com.hbwl.ai.listener;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;

public class CustomChatModelListener implements ChatModelListener {

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        System.out.println("--------------------------------------");
        System.out.println("发起模型调用请求");
        System.out.println("请求模型: " + requestContext.modelProvider());
        System.out.println("--------------------------------------");
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        System.out.println("--------------------------------------");
        System.out.println("模型调用成功");
        System.out.println("消耗Token: " + responseContext.chatResponse().tokenUsage().toString());
        System.out.println("--------------------------------------");
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        System.out.println("--------------------------------------");
        System.out.println("模型调用出错: " + errorContext.toString());
        System.out.println("--------------------------------------");
    }
}
