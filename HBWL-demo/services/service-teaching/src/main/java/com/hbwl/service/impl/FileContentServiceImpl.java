package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.FileContentMapper;
import com.hbwl.pojo.FileContent;
import com.hbwl.service.FileContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class FileContentServiceImpl implements FileContentService {

    //数据库中记录文件内容的相关信息，文件本体存储在本地
    @Autowired
    private FileContentMapper fileContentMapper;

    @Autowired
    private Path fileStorageLocation;

    @Override
    public int addFileContent(FileContent fileContent, MultipartFile file) {
        if (fileContent == null || file == null || fileContent.getName() == null) return -1;
        String fileName = storeFile(file);
        fileContent.setName(fileName);
        return fileContentMapper.insert(fileContent);
    }

    @Override
    public int deleteFileContent(FileContent fileContent) {
        if (fileContent == null || fileContent.getName() == null ||
                fileContent.getId() == null) return -1;
        boolean removeFile = removeFile(fileContent.getName());
        if (removeFile) {
            return fileContentMapper.deleteById(fileContent.getId());
        }
        return 0;
    }

    @Override
    public int deleteFileContentById(Integer id, String fileName) {
        boolean removeFile = removeFile(fileName);
        if (removeFile) {
            return fileContentMapper.deleteById(id);
        }
        return 0;
    }

    @Override
    public int updateFileContentById(FileContent fileContent, MultipartFile file) {
        if (fileContent == null || file == null || fileContent.getId() == null ||
                fileContent.getName() == null) return -1;
        String fileName = storeFile(file);
        fileContent.setName(fileName);
        UpdateWrapper<FileContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", fileContent.getId());
        updateWrapper.set("name", fileName);
        if (fileContent.getType() != null) updateWrapper.set("type", fileContent.getType());
        if (fileContent.getSize() != null) updateWrapper.set("size", fileContent.getSize());
        if (fileContent.getMatId() != null) updateWrapper.set("matId", fileContent.getMatId());
        return fileContentMapper.update(null, updateWrapper);
    }

    @Override
    public List<FileContent> getFileContents(FileContent fileContent) {
        if (fileContent == null) return fileContentMapper.selectList(null);
        QueryWrapper<FileContent> queryWrapper = new QueryWrapper<>();
        if (fileContent.getId() != null) queryWrapper.eq("id", fileContent.getId());
        if (fileContent.getName() != null) queryWrapper.like("name", fileContent.getName());
        if (fileContent.getType() != null) queryWrapper.eq("type", fileContent.getType());
        if (fileContent.getMatId() != null) queryWrapper.eq("matId", fileContent.getMatId());
        if (fileContent.getSize() != null) queryWrapper.le("size", fileContent.getSize());
        return fileContentMapper.selectList(queryWrapper);
    }

    @Override
    public Page<FileContent> getFileContentsPage(int pageNo, int pageSize, FileContent fileContent) {
        Page<FileContent> page = new Page<>(pageNo, pageSize);
        if (fileContent == null) return fileContentMapper.selectPage(page, null);
        QueryWrapper<FileContent> queryWrapper = new QueryWrapper<>();
        if (fileContent.getId() != null) queryWrapper.eq("id", fileContent.getId());
        if (fileContent.getName() != null) queryWrapper.like("name", fileContent.getName());
        if (fileContent.getType() != null) queryWrapper.eq("type", fileContent.getType());
        if (fileContent.getMatId() != null) queryWrapper.eq("matId", fileContent.getMatId());
        if (fileContent.getSize() != null) queryWrapper.le("size", fileContent.getSize());
        return fileContentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Resource loadFileContent(String fileName) {
        if (fileName == null) return null;
        return loadFile(fileName);
    }

    //将文件存储到本地，返回文件名称
    private String storeFile(MultipartFile file) {
        try {
            //生成唯一文件名
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = "";
            if (originalFileName.contains(".")){
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID() + fileExtension;

            Path targetLocation = fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败: " + file.getOriginalFilename(), e);
        }
    }

    //根据url将文件加载为Resource
    private Resource loadFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()){
                return resource;
            } else {
                throw new RuntimeException("文件不存在: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("文件不存在: " + fileName, e);
        }
    }

    //根据url将文件移除
    private boolean removeFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败: " + fileName, e);
        }
    }
}
