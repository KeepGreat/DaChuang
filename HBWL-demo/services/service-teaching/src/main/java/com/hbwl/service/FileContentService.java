package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.FileContent;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileContentService {

    int addFileContent(FileContent fileContent, MultipartFile file);

    int deleteFileContentById(Integer id);

    int updateFileContentById(FileContent fileContent, MultipartFile file);

    List<FileContent> getFileContents(FileContent fileContent);

    Page<FileContent> getFileContentsPage(int pageNo, int pageSize, FileContent fileContent);

    //资料:文件内容 = 1:1
    Resource loadFileContent(Integer id);
}
