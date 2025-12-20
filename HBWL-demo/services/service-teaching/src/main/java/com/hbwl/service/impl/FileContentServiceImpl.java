package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.FileContentMapper;
import com.hbwl.pojo.FileContent;
import com.hbwl.service.FileContentService;
import com.hbwl.utils.FileContentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class FileContentServiceImpl implements FileContentService {

    //数据库中记录文件内容的相关信息，文件本体存储在本地
    @Autowired
    private FileContentMapper fileContentMapper;

    @Autowired
    private FileContentUtil fileContentUtil;

    @Override
    public int addFileContent(FileContent fileContent, MultipartFile file) {
        if (fileContent == null || file == null) return -1;
        String fileName = fileContentUtil.storeFile(file);
        fileContent.setName(fileName);
        fileContentUtil.embedVector(fileContent, file);
        return fileContentMapper.insert(fileContent);
    }

    @Override
    public int deleteFileContentById(Integer id) {
        if (id == null) return -1;
        String fileName = fileContentMapper.selectById(id).getName();
        FileContent fileContent = new FileContent();
        fileContent.setName(fileName);
        fileContentUtil.removeVector(fileContent);
        boolean removeFile = fileContentUtil.removeFile(fileName);
        if (removeFile) {
            return fileContentMapper.deleteById(id);
        }
        return 0;
    }

    @Override
    public int updateFileContentById(FileContent fileContent, MultipartFile file) {
        if (fileContent == null || file == null || fileContent.getId() == null ||
                fileContent.getName() == null) return -1;
        fileContentUtil.removeVector(fileContent);
        String oldFileName = fileContentMapper.selectById(fileContent.getId()).getName();
        fileContentUtil.removeFile(oldFileName);
        String fileName = fileContentUtil.storeFile(file);
        fileContent.setName(fileName);
        fileContentUtil.embedVector(fileContent, file);
        UpdateWrapper<FileContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", fileContent.getId());
        updateWrapper.set("name", fileName);
        if (fileContent.getType() != null) updateWrapper.set("type", fileContent.getType());
        if (fileContent.getSize() != null) updateWrapper.set("size", fileContent.getSize());
        if (fileContent.getMatId() != null) updateWrapper.set("mat_id", fileContent.getMatId());
        return fileContentMapper.update(null, updateWrapper);
    }

    @Override
    public List<FileContent> getFileContents(FileContent fileContent) {
        if (fileContent == null) return fileContentMapper.selectList(null);
        QueryWrapper<FileContent> queryWrapper = new QueryWrapper<>();
        if (fileContent.getId() != null) queryWrapper.eq("id", fileContent.getId());
        if (fileContent.getName() != null) queryWrapper.eq("name", fileContent.getName());
        if (fileContent.getType() != null) queryWrapper.eq("type", fileContent.getType());
        if (fileContent.getMatId() != null) queryWrapper.eq("mat_id", fileContent.getMatId());
        if (fileContent.getSize() != null) queryWrapper.le("size", fileContent.getSize());
        return fileContentMapper.selectList(queryWrapper);
    }

    @Override
    public Page<FileContent> getFileContentsPage(int pageNo, int pageSize, FileContent fileContent) {
        Page<FileContent> page = new Page<>(pageNo, pageSize);
        if (fileContent == null) return fileContentMapper.selectPage(page, null);
        QueryWrapper<FileContent> queryWrapper = new QueryWrapper<>();
        if (fileContent.getId() != null) queryWrapper.eq("id", fileContent.getId());
        if (fileContent.getName() != null) queryWrapper.eq("name", fileContent.getName());
        if (fileContent.getType() != null) queryWrapper.eq("type", fileContent.getType());
        if (fileContent.getMatId() != null) queryWrapper.eq("mat_id", fileContent.getMatId());
        if (fileContent.getSize() != null) queryWrapper.le("size", fileContent.getSize());
        return fileContentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Resource loadFileContent(Integer id) {
        if (id == null) return null;
        String fileName = fileContentMapper.selectById(id).getName();
        return fileContentUtil.loadFile(fileName);
    }
}
