package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.VideoContent;
import com.hbwl.service.VideoContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching/video")
public class VideoContentController {

    @Autowired
    private VideoContentService videoContentService;

    //流式播放视频（支持断点续传）
    @GetMapping("/stream")
    public ResponseEntity<Resource> streamVideo(@RequestParam String fileName,
                                                @RequestHeader(value = "Range", required = false) String rangeHeader){
        return videoContentService.getVideoStream(fileName, rangeHeader);
    }

    @PostMapping
    public Result addVideoContent(@RequestBody VideoContent videoContent,
                                  @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        VideoContent addedVideoContent = videoContentService.addVideoContent(videoContent);
        if (addedVideoContent == null) return Result.error("增加视频信息失败");
        return Result.success(addedVideoContent, null);
    }

    @DeleteMapping("/{id}")
    public Result deleteVideoContent(@PathVariable Integer id,
                                     @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = videoContentService.deleteVideoContentById(id);
        if (row == 0) return Result.error("删除视频失败");
        return Result.success("删除视频成功");
    }

    @PutMapping
    public Result updateVideoContent(@RequestBody VideoContent videoContent,
                                     @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = videoContentService.updateVideoContent(videoContent);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新视频信息失败");
        return Result.success("更新视频信息成功");
    }

    @GetMapping
    public Result getVideoContents(@RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) Integer matId,
                                   @RequestParam(required = false) Long size){
        VideoContent videoContent = new VideoContent();
        videoContent.setId(id);
        videoContent.setMatId(matId);
        videoContent.setSize(size);
        List<VideoContent> list = videoContentService.getVideoContents(videoContent);
        if (list == null || list.isEmpty()) return Result.error("查询视频信息失败");
        return Result.success(list, "查询视频信息成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getVideoContentsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                       @RequestParam(required = false) Integer id,
                                       @RequestParam(required = false) Integer matId,
                                       @RequestParam(required = false) Long size){
        VideoContent videoContent = new VideoContent();
        videoContent.setId(id);
        videoContent.setMatId(matId);
        videoContent.setSize(size);
        Page<VideoContent> page = videoContentService.getVideoContentsPage(pageNo, pageSize, videoContent);
        if (page == null || page.getSize() == 0) return Result.error("查询视频信息失败");
        return Result.success(page, "查询视频信息成功");
    }
}
