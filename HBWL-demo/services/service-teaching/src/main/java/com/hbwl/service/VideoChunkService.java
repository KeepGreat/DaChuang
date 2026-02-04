package com.hbwl.service;

import com.hbwl.pojo.VideoChunk;
import com.hbwl.pojo.VideoContent;
import org.springframework.web.multipart.MultipartFile;

public interface VideoChunkService {
    //检查分片是否存在
    boolean checkChunk(Integer videoId, Integer chunkNumber);
    //上传分片
    int uploadChunk(VideoChunk videoChunk, MultipartFile chunkFile);
    //合并分片
    int mergeChunk(VideoContent videoContent);
    //取消上传
    int deleteChunk(Integer videoId);

    void deleteChunkAuto();

    //获取上传进度
    int getUploadedChunks(Integer videoId);
}
