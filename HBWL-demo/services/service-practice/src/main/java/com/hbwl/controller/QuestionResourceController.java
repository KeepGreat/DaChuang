package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbwl.pojo.QuestionResource;
import com.hbwl.service.QuestionResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/practice/questionresource")
public class QuestionResourceController {

    @Autowired
    private QuestionResourceService questionResourceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addQuestionResource(@RequestPart("questionResourceJSON") String questionResourceJSON,
                                      @RequestPart("resource") MultipartFile resource){
        try {
            ObjectMapper mapper = new ObjectMapper();
            QuestionResource questionResource = mapper.readValue(questionResourceJSON, QuestionResource.class);

            int row = questionResourceService.addQuestionResource(questionResource, resource);
            if (row == -1) return "参数不能为空";
            if (row == 0) return "资源接收成功，但在保存时失败了";
            return "增加资源成功";
        } catch (Exception e){
            return "资源上传失败" + e.getMessage();
        }
    }

    @DeleteMapping("{id}")
    public String deleteQuestionResource(@PathVariable("id") Integer id){
        int row = questionResourceService.deleteQuestionResourceById(id);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "删除资源失败";
        return "删除资源成功";
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateQuestionResource(@RequestPart("questionResourceJSON") String questionResourceJSON,
                                         @RequestPart("resource") MultipartFile resource){
        try {
            ObjectMapper mapper = new ObjectMapper();
            QuestionResource questionResource = mapper.readValue(questionResourceJSON, QuestionResource.class);
            int row = questionResourceService.updateQuestionResourceById(questionResource, resource);
            if (row == -1) return "参数不能为空";
            if (row == 0) return "资源上传成功，但在更新时失败了";
            return "资源更新成功";
        } catch (Exception e){
            return "资源上传失败" + e.getMessage();
        }
    }

    @GetMapping
    public List<QuestionResource> getQuestionResources(@RequestParam(required = false) Integer id,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Integer type,
                                                       @RequestParam(required = false) Integer size,
                                                       @RequestParam(required = false) Integer questionId){
        QuestionResource questionResource = new QuestionResource();
        questionResource.setId(id);
        questionResource.setName(name);
        questionResource.setType(type);
        questionResource.setSize(size);
        questionResource.setQuestionId(questionId);
        return questionResourceService.getQuestionResources(questionResource);
    }

    @GetMapping("/{page}/{size}")
    public Page<QuestionResource> getQuestionResourcesPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                                           @RequestParam(required = false) Integer id,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(required = false) Integer type,
                                                           @RequestParam(required = false) Integer size,
                                                           @RequestParam(required = false) Integer questionId){
        QuestionResource questionResource = new QuestionResource();
        questionResource.setId(id);
        questionResource.setName(name);
        questionResource.setType(type);
        questionResource.setSize(size);
        questionResource.setQuestionId(questionId);
        return questionResourceService.getQuestionResourcesPage(pageNo, pageSize, questionResource);
    }
}
