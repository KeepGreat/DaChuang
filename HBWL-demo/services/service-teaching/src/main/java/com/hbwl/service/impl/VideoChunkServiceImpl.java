package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hbwl.mapper.VideoChunkMapper;
import com.hbwl.mapper.VideoContentMapper;
import com.hbwl.pojo.VideoChunk;
import com.hbwl.pojo.VideoContent;
import com.hbwl.service.VideoChunkService;
import com.hbwl.utils.VideoContentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@EnableScheduling
public class VideoChunkServiceImpl implements VideoChunkService {

    @Autowired
    private VideoChunkMapper videoChunkMapper;

    @Autowired
    private VideoContentMapper videoContentMapper;

    @Value("${video.upload.max-chunk-size}")
    private Long maxChunkSize;

    @Autowired
    private VideoContentUtil videoContentUtil;

    @Override
    public boolean checkChunk(Integer videoId, Integer chunkNumber) {
        QueryWrapper<VideoChunk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        queryWrapper.eq("number", chunkNumber);
        return videoChunkMapper.exists(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int uploadChunk(VideoChunk videoChunk, MultipartFile chunkFile) {
        if (videoChunk == null ||
            videoChunk.getVideoId() == null || videoChunk.getNumber() == null || videoChunk.getTotal() == null)
            return -1;

        // 验证分片大小
        if (videoChunk.getSize() > maxChunkSize) {
            throw new RuntimeException("分片大小超过限制");
        }

        Integer videoId = videoChunk.getVideoId();
        Integer chunkNumber = videoChunk.getNumber();

        // 检查是否已上传
        if (checkChunk(videoId, chunkNumber)) {
            log.info("分片已存在: videoId={}, chunkNumber={}",
                    videoId, chunkNumber);
            return 0;
        }

        try {
            String chunkFileName = videoContentUtil.storeVideoChunk(chunkFile);
            videoChunk.setName(chunkFileName);
            int row = videoChunkMapper.insert(videoChunk);
            log.info("分片上传成功 videoId={}, chunkNumber={}, size={}",
                    videoId, chunkNumber, videoChunk.getSize());
            return row;
        } catch (Exception e) {
            log.error("分片上传失败", e);
            throw new RuntimeException("分片上传失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int mergeChunk(VideoContent videoContent) {
        if (videoContent == null ||
            videoContent.getId() == null || videoContent.getName() == null)
            return -1;
        // 获取所有分片信息
        Integer videoId = videoContent.getId();
        QueryWrapper<VideoChunk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        List<VideoChunk> chunks = videoChunkMapper.selectList(queryWrapper);
        if (chunks == null || chunks.isEmpty()){
            log.info("合并分片失败，未找到相关视频的分片: videoId={}",videoId);
            return 0;
        }
        try {
            videoContentUtil.mergeVideoChunk(chunks, videoContent);
            log.info("分片合并完成: videoId={}, size={}, count={}",
                    videoId, videoContent.getSize(), chunks.size());
            return chunks.size();
        } catch (Exception e){
            log.error("分片合并失败", e);
            throw new RuntimeException("分片合并失败", e);
        }
    }

    @Override
    public int deleteChunk(Integer videoId) {
        if (videoId == null) return -1;
        QueryWrapper<VideoChunk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        List<VideoChunk> chunks = videoChunkMapper.selectList(queryWrapper);
        if (chunks == null || chunks.isEmpty()) return 0;
        for (VideoChunk chunk : chunks){
            videoContentUtil.removeVideoChunk(chunk.getName());
        }
        return videoChunkMapper.delete(queryWrapper) + videoContentMapper.deleteById(videoId);
    }

    @Override
    public int getUploadedChunks(Integer videoId) {
        if (videoId == null) return -1;
        QueryWrapper<VideoChunk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        return Math.toIntExact(videoChunkMapper.selectCount(queryWrapper));
    }

    //自动清理废弃分片
    @Scheduled(cron = "0 */12 * * * ?") //每12分钟执行一次
    @Override
    public void deleteChunkAuto(){
        log.info("开始批量清理过期分片");
        QueryWrapper<VideoChunk> queryWrapper = new QueryWrapper<>();
        //如果这个分片是标记可被删除或一天前的就进行删除
        queryWrapper.eq("can_deleted", true).or().le("created_at", LocalDateTime.now().minusDays(1));
        List<VideoChunk> chunks = videoChunkMapper.selectList(queryWrapper);
        for (VideoChunk chunk : chunks){
            videoContentUtil.removeVideoChunk(chunk.getName());
        }
        int row = videoChunkMapper.delete(queryWrapper);
        log.info("完成批量清理过期分片，共清理分片数：{}", row);
    }
}
