package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.VideoContent;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VideoContentService {

    //获取视频流（支持断点续传）
    ResponseEntity<Resource> getVideoStream(String filename, String rangeHeaders);

    //注意该方法要返回创建后的Id
    VideoContent addVideoContent(VideoContent videoContent);

    //注意，取消分片上传时无需再调用此方法
    int deleteVideoContentById(Integer id);

    //此方法不支持更改视频文件,目前只支持修改matId
    int updateVideoContent(VideoContent videoContent);

    List<VideoContent> getVideoContents(VideoContent videoContent);

    Page<VideoContent> getVideoContentsPage(int pageNo, int pageSize, VideoContent videoContent);
}
