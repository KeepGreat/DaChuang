package com.hbwl.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hbwl.ai.service.PromptService;
import com.hbwl.ai.utils.RuntimePromptManager;
import com.hbwl.mapper.PromptInfoMapper;
import com.hbwl.pojo.ai.PromptInfo;
import com.hbwl.ai.utils.PromptStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PromptServiceImpl implements PromptService {

    @Autowired
    private PromptInfoMapper promptInfoMapper;

    @Autowired
    private PromptStorageUtil promptStorageUtil;

    @Autowired
    private RuntimePromptManager runtimePromptManager;

    @Override
    public int addPrompt(PromptInfo promptInfo, String prompt) {
        if (promptInfo == null || prompt == null || promptInfo.getCreatorId() == null) {
            return -1;
        }
        String name = promptStorageUtil.savePromptToTxt(prompt);
        promptInfo.setName(name);
        return promptInfoMapper.insert(promptInfo);
    }

    @Override
    public int deletePrompt(Integer id) {
        if (id == null || id == 0) {
            return -1;
        }
        if (!promptStorageUtil.deletePromptTxt(id)) return 0;
        if (Objects.equals(runtimePromptManager.getRuntimePromptId(), id)){
            runtimePromptManager.setRuntimePromptId(0);
            runtimePromptManager.evict(id); //清除缓存后会再读同id文件，若无则报错
        }
        return promptInfoMapper.deleteById(id);
    }

    @Override
    public int updatePrompt(PromptInfo promptInfo, String prompt) {
        if (promptInfo == null || promptInfo.getId() == null) {
            return -1;
        }
        UpdateWrapper<PromptInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", promptInfo.getId());
        if (promptInfo.getDescription() != null) updateWrapper.set("description", promptInfo.getDescription());
        int row = promptInfoMapper.update(null, updateWrapper);
        if (row == 0) return 0;
        if (prompt != null) {
            promptStorageUtil.deletePromptTxt(promptInfo.getId());
            String newFilename = promptStorageUtil.savePromptToTxt(prompt);
            updateWrapper.set("name", newFilename);
            if (Objects.equals(runtimePromptManager.getRuntimePromptId(), promptInfo.getId())){
                runtimePromptManager.evict(promptInfo.getId());
            }
        }
        return row;
    }

    @Override
    public List<PromptInfo> getPromptInfos(Integer id, String creatorId) {
        if (id == null && creatorId == null) {
            return promptInfoMapper.selectList(null);
        }
        QueryWrapper<PromptInfo> queryWrapper = new QueryWrapper<>();
        if (id != null) queryWrapper.eq("id", id);
        if (creatorId != null) queryWrapper.eq("creator_id", creatorId);
        return promptInfoMapper.selectList(queryWrapper);
    }

    @Override
    public String getPrompt(Integer id) {
        if (id == null){
            return null;
        }
        return promptStorageUtil.loadPromptFromTxt(id);
    }

    @Override
    public boolean setRuntimePrompt(Integer id) {
        return runtimePromptManager.setRuntimePromptId(id);
    }

    @Override
    public String getRuntimePrompt() {
        return runtimePromptManager.getRuntimePrompt();
    }
}
