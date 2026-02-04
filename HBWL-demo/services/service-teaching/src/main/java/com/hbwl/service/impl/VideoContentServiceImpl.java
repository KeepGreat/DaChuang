package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.VideoContentMapper;
import com.hbwl.pojo.VideoContent;
import com.hbwl.service.VideoContentService;
import com.hbwl.utils.VideoContentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VideoContentServiceImpl implements VideoContentService {

    @Autowired
    private VideoContentMapper videoContentMapper;

    @Autowired
    private VideoContentUtil videoContentUtil;

    @Override
    public ResponseEntity<Resource> getVideoStream(String filename, String rangeHeaders) {
        try {
            return videoContentUtil.loadVideo(filename, rangeHeaders);
        } catch (Exception e){
            log.error("获取视频流失败", e);
            throw new RuntimeException("获取视频流失败", e);
        }
    }

    @Override
    public VideoContent addVideoContent(VideoContent videoContent) {
        if (videoContent == null || videoContent.getMatId() == null) return null;
        String originalVideoName = videoContent.getName();
        String newVideoName = UUID.randomUUID() + originalVideoName.substring(originalVideoName.lastIndexOf("."));
        videoContent.setName(newVideoName);
        int row = videoContentMapper.insert(videoContent);
        if (row > 0){
            return videoContent;
        }
        else return null;
    }

    @Override
    public int deleteVideoContentById(Integer id) {
        if (id == null) return -1;
        String videoFileName = videoContentMapper.selectById(id).getName();
        if (videoContentUtil.removeVideo(videoFileName)){
            return videoContentMapper.deleteById(id);
        }
        else return 0;
    }

    @Override
    public int updateVideoContent(VideoContent videoContent) {
        if (videoContent == null || videoContent.getId() == null) return -1;
        UpdateWrapper<VideoContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", videoContent.getId());
        if (videoContent.getMatId() != null) updateWrapper.set("mat_id", videoContent.getMatId());
        return videoContentMapper.update(null, updateWrapper);
    }

    @Override
    public List<VideoContent> getVideoContents(VideoContent videoContent) {
        if (videoContent == null) return videoContentMapper.selectList(null);
        QueryWrapper<VideoContent> queryWrapper = new QueryWrapper<>();
        if (videoContent.getId() != null) queryWrapper.eq("id", videoContent.getId());
        if (videoContent.getMatId() != null) queryWrapper.eq("mat_id", videoContent.getMatId());
        if (videoContent.getSize() != null) queryWrapper.le("size", videoContent.getSize());
        return videoContentMapper.selectList(queryWrapper);
    }

    @Override
    public Page<VideoContent> getVideoContentsPage(int pageNo, int pageSize, VideoContent videoContent) {
        Page<VideoContent> page = new Page<>(pageNo, pageSize);
        if (videoContent == null) return videoContentMapper.selectPage(page, null);
        QueryWrapper<VideoContent> queryWrapper = new QueryWrapper<>();
        if (videoContent.getId() != null) queryWrapper.eq("id", videoContent.getId());
        if (videoContent.getMatId() != null) queryWrapper.eq("mat_id", videoContent.getMatId());
        if (videoContent.getSize() != null) queryWrapper.le("size", videoContent.getSize());
        return videoContentMapper.selectPage(page, queryWrapper);
    }
    //私有方法

}
