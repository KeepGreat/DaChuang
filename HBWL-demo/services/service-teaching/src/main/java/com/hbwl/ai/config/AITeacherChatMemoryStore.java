package com.hbwl.ai.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hbwl.mapper.AgentChatMemoryMapper;
import com.hbwl.pojo.ai.AgentChatMemory;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AITeacherChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private AgentChatMemoryMapper agentChatMemoryMapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        QueryWrapper<AgentChatMemory> wrapper = new QueryWrapper<>();
        wrapper.eq("memory_id", (String)memoryId);
        List<AgentChatMemory> customChatMemories = agentChatMemoryMapper.selectList(wrapper);
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (AgentChatMemory customChatMemory : customChatMemories) {
            chatMessages.addAll(ChatMessageDeserializer.messagesFromJson(customChatMemory.getContent()));
        }
        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        QueryWrapper<AgentChatMemory> wrapper = new QueryWrapper<>();
        wrapper.eq("memory_id", (String)memoryId);
        String content = ChatMessageSerializer.messagesToJson(list);
        AgentChatMemory agentChatMemory = new AgentChatMemory();
        agentChatMemory.setMemoryId((String)memoryId);
        agentChatMemory.setContent(content);
        if (agentChatMemoryMapper.selectCount(wrapper) == 0){
            agentChatMemoryMapper.insert(agentChatMemory);
            return;
        }
        agentChatMemoryMapper.update(agentChatMemory, wrapper);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        QueryWrapper<AgentChatMemory> wrapper = new QueryWrapper<>();
        wrapper.eq("memory_id", (String)memoryId);
        agentChatMemoryMapper.delete(wrapper);
    }
}
