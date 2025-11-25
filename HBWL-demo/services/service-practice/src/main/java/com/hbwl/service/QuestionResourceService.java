package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.QuestionResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionResourceService {

    int addQuestionResource(QuestionResource questionResource, MultipartFile file);

    int deleteQuestionResourceById(Integer id);

    int updateQuestionResourceById(QuestionResource questionResource, MultipartFile file);

    List<QuestionResource> getQuestionResources(QuestionResource questionResource);

    Page<QuestionResource> getQuestionResourcesPage(int pageNo, int pageSize, QuestionResource questionResource);

    Resource loadQuestionResourceById(Integer id);
}
