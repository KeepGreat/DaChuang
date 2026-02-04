package com.hbwl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbwl.common.Result;
import com.hbwl.pojo.VideoChunk;
import com.hbwl.pojo.VideoContent;
import com.hbwl.service.VideoChunkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teaching/video/chunk")
public class VideoChunkController {

    @Autowired
    private VideoChunkService videoChunkService;

    //检查分片是否存在（秒传/断点续传）
    @GetMapping("/check")
    public Result checkChunk(@RequestParam Integer videoId,
                             @RequestParam Integer chunkNumber,
                             @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        boolean checkChunk = videoChunkService.checkChunk(videoId, chunkNumber);
        return Result.success(checkChunk, null);
    }

    //上传分片
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadChunk(@RequestPart("videoChunk") String videoChunkJSON,
                              @RequestPart("chunk") MultipartFile chunk,
                              @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            VideoChunk videoChunk = objectMapper.readValue(videoChunkJSON, VideoChunk.class);

            int row = videoChunkService.uploadChunk(videoChunk, chunk);
            if (row == -1) return Result.error("参数不能为空");
            if (row == 0) return Result.error("文件接收成功，但在保存时失败了");
            return Result.success("文件上传成功");
        } catch (Exception e) {
            return Result.error("文件上传失败" + e.getMessage());
        }
    }

    //合并分片
    @PostMapping("/merge")
    public Result mergeChunk(@RequestBody VideoContent videoContent,
                             @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        int row = videoChunkService.mergeChunk(videoContent);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("请求接收成功，但在合并时失败了");
        return Result.success("合并成功");
    }

    @DeleteMapping("/{id}")
    public Result deleteChunk(@PathVariable("id") Integer videoId,
                              @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        int row = videoChunkService.deleteChunk(videoId);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除分片失败");
        return Result.success("删除成功");
    }

    @GetMapping("/count")
    public Result getUploadedChunkCount(@RequestParam Integer videoId,
                                        @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) Result.error("权限不足");
        int count = videoChunkService.getUploadedChunks(videoId);
        return Result.success(count, null);
    }
}
