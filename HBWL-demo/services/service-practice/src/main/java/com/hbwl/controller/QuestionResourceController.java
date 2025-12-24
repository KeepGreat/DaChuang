package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbwl.common.Result;
import com.hbwl.pojo.QuestionResource;
import com.hbwl.service.QuestionResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/practice/questionresource")
public class QuestionResourceController {

    @Autowired
    private QuestionResourceService questionResourceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result addQuestionResource(@RequestPart("questionResourceJSON") String questionResourceJSON,
                                      @RequestPart("resource") MultipartFile resource,
                                      @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        try {
            ObjectMapper mapper = new ObjectMapper();
            QuestionResource questionResource = mapper.readValue(questionResourceJSON, QuestionResource.class);

            int row = questionResourceService.addQuestionResource(questionResource, resource);
            if (row == -1) return Result.error("参数不能为空");
            if (row == 0) return Result.error("资源接收成功，但在保存时失败了");
            return Result.success("增加资源成功");
        } catch (Exception e){
            return Result.error("资源上传失败" + e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public Result deleteQuestionResource(@PathVariable("id") Integer id,
                                         @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = questionResourceService.deleteQuestionResourceById(id);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除资源失败");
        return Result.success("删除资源成功");
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateQuestionResource(@RequestPart("questionResourceJSON") String questionResourceJSON,
                                         @RequestPart("resource") MultipartFile resource,
                                         @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        try {
            ObjectMapper mapper = new ObjectMapper();
            QuestionResource questionResource = mapper.readValue(questionResourceJSON, QuestionResource.class);
            int row = questionResourceService.updateQuestionResourceById(questionResource, resource);
            if (row == -1) return Result.error("参数不能为空");
            if (row == 0) return Result.error("资源上传成功，但在更新时失败了");
            return Result.success("资源更新成功");
        } catch (Exception e){
            return Result.error("资源上传失败" + e.getMessage());
        }
    }

    @GetMapping
    public Result getQuestionResources(@RequestParam(required = false) Integer id,
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
        List<QuestionResource> list = questionResourceService.getQuestionResources(questionResource);
        if (list.isEmpty()) return Result.error("查询问题资源记录失败");
        return Result.success(list, "查询问题资源记录成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getQuestionResourcesPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
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
        Page<QuestionResource> page = questionResourceService.getQuestionResourcesPage(pageNo, pageSize, questionResource);
        if (page.getSize() == 0) return Result.error("查询问题资源记录失败");
        return Result.success(page, "查询问题资源记录成功");
    }

    //不建议包装，会导致传输效率低下
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadResource(@RequestParam Integer id){
        try {
            Resource resource = questionResourceService.loadQuestionResourceById(id);

            // 使用Spring的MediaTypeFactory获取MIME类型
            String contentType = MediaTypeFactory.getMediaType(resource)
                    .map(MediaType::toString)
                    .orElse("application/octet-stream");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
