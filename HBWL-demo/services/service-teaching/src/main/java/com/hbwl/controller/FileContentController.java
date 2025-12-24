package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbwl.common.Result;
import com.hbwl.pojo.FileContent;
import com.hbwl.service.FileContentService;
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
@RequestMapping("/api/teaching/file")
public class FileContentController {

    @Autowired
    private FileContentService fileContentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadFile(@RequestPart("fileContent") String fileContentJSON,
                             @RequestPart("file") MultipartFile file,
                             @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileContent fileContent = objectMapper.readValue(fileContentJSON, FileContent.class);

            int row = fileContentService.addFileContent(fileContent, file);
            if (row == -1) return Result.error("参数不能为空");
            if (row == 0) return Result.error("文件接收成功，但在保存时失败了");
            return Result.success("文件上传成功");
        } catch (Exception e) {
            return Result.error("文件上传失败" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteFile(@PathVariable("id") Integer id,
                             @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        int row = fileContentService.deleteFileContentById(id);
        if (row == 0) return Result.error("删除文件失败");
        return Result.success("删除文件成功");
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateFile(@RequestPart("fileContent") String fileContentJSON,
                             @RequestPart("file") MultipartFile file,
                             @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        // fileContent nullable + multipartFile
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileContent fileContent = objectMapper.readValue(fileContentJSON, FileContent.class);

            int row = fileContentService.updateFileContentById(fileContent, file);
            if (row == -1) return Result.error("参数不能为空");
            if (row == 0) return Result.error("在更新文件时接收成功，但更新失败了");
            return Result.success("更新文件成功");
        } catch (Exception e) {
            return Result.error("更新文件失败" + e.getMessage());
        }
    }

    @GetMapping
    public Result getFileContents(@RequestParam(required = false) Integer id,
                                  @RequestParam(required = false) String type,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) Integer size,
                                  @RequestParam(required = false) Integer matId){
        FileContent fileContent = new FileContent();
        fileContent.setId(id);
        fileContent.setMatId(matId);
        fileContent.setType(type);
        fileContent.setName(name);
        fileContent.setSize(size);
        List<FileContent> list = fileContentService.getFileContents(fileContent);
        if (list == null || list.isEmpty()) return Result.error("查询文件信息失败");
        return Result.success(list, "查询文件信息成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getFileContentsPage(@PathVariable("page") Integer pageNo, @PathVariable("size") Integer pageSize,
                                      @RequestParam(required = false) Integer id,
                                      @RequestParam(required = false) String type,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false) Integer size,
                                      @RequestParam(required = false) Integer matId){
        FileContent fileContent = new FileContent();
        fileContent.setId(id);
        fileContent.setMatId(matId);
        fileContent.setType(type);
        fileContent.setName(name);
        fileContent.setSize(size);
        Page<FileContent> page = fileContentService.getFileContentsPage(pageNo, pageSize, fileContent);
        if (page == null || page.getSize() == 0) return Result.error("查询文件信息失败");
        return Result.success(page, "查询文件信息成功");
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam Integer id){
        try {
            Resource resource = fileContentService.loadFileContent(id);

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
