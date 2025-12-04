package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.QuestionResourceMapper;
import com.hbwl.pojo.QuestionResource;
import com.hbwl.service.QuestionResourceService;
import com.hbwl.utils.QuestionResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class QuestionResourceServiceImpl implements QuestionResourceService {

    @Autowired
    private QuestionResourceMapper questionResourceMapper;

    @Autowired
    private QuestionResourceUtil questionResourceUtil;

    @Override
    public int addQuestionResource(QuestionResource questionResource, MultipartFile file) {
        if (questionResource == null || file == null) return -1;
        String ResourceName = questionResourceUtil.storeFile(file);
        questionResource.setName(ResourceName);
        return questionResourceMapper.insert(questionResource);
    }

    @Override
    public int deleteQuestionResourceById(Integer id) {
        if (id == null) return -1;
        String ResourceName = questionResourceMapper.selectById(id).getName();
        boolean removeFile = questionResourceUtil.removeFile(ResourceName);
        if (removeFile) {
            return questionResourceMapper.deleteById(id);
        }
        else return 0;
    }

    @Override
    public int updateQuestionResourceById(QuestionResource questionResource, MultipartFile file) {
        if (questionResource == null || file == null || questionResource.getId() == null) return -1;
        String oldResourceName = questionResourceMapper.selectById(questionResource.getId()).getName();
        questionResourceUtil.removeFile(oldResourceName);
        String newResourceName = questionResourceUtil.storeFile(file);
        questionResource.setName(newResourceName);
        UpdateWrapper<QuestionResource> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionResource.getId());
        updateWrapper.set("name", questionResource.getName());
        if (questionResource.getDescription() != null) updateWrapper.set("description", questionResource.getDescription());
        if (questionResource.getType() != null) updateWrapper.set("type", questionResource.getType());
        if (questionResource.getSize() != null) updateWrapper.set("size", questionResource.getSize());
        if (questionResource.getQuestionId() != null) updateWrapper.set("question_id", questionResource.getQuestionId());
        return questionResourceMapper.update(null, updateWrapper);
    }

    @Override
    public List<QuestionResource> getQuestionResources(QuestionResource questionResource) {
        if (questionResource == null) return questionResourceMapper.selectList(null);
        QueryWrapper<QuestionResource> queryWrapper = new QueryWrapper<>();
        if (questionResource.getId() != null) queryWrapper.eq("id", questionResource.getId());
        if (questionResource.getName() != null) queryWrapper.eq("name", questionResource.getName());
        if (questionResource.getType() != null) queryWrapper.eq("type", questionResource.getType());
        if (questionResource.getSize() != null) queryWrapper.le("size", questionResource.getSize());
        if (questionResource.getQuestionId() != null) queryWrapper.eq("question_id", questionResource.getQuestionId());
        return questionResourceMapper.selectList(queryWrapper);
    }

    @Override
    public Page<QuestionResource> getQuestionResourcesPage(int pageNo, int pageSize, QuestionResource questionResource) {
        Page<QuestionResource> page = new Page<>(pageNo, pageSize);
        if (questionResource == null) return questionResourceMapper.selectPage(page, null);
        QueryWrapper<QuestionResource> queryWrapper = new QueryWrapper<>();
        if (questionResource.getId() != null) queryWrapper.eq("id", questionResource.getId());
        if (questionResource.getName() != null) queryWrapper.eq("name", questionResource.getName());
        if (questionResource.getType() != null) queryWrapper.eq("type", questionResource.getType());
        if (questionResource.getSize() != null) queryWrapper.le("size", questionResource.getSize());
        if (questionResource.getQuestionId() != null) queryWrapper.eq("question_id", questionResource.getQuestionId());
        return questionResourceMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Resource loadQuestionResourceById(Integer id) {
        String ResourceName = questionResourceMapper.selectById(id).getName();
        return questionResourceUtil.loadFile(ResourceName);
    }
}
