package com.hbwl.ai.utils;

import com.hbwl.mapper.PromptInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
public class PromptStorageUtil {

    @Qualifier("prompt")
    @Autowired
    private Path promptStorageLocation;

    @Autowired
    private PromptInfoMapper promptInfoMapper;

    //将String存储为txt文件, 返回文件名
    public String savePromptToTxt(String prompt){
        try {
            Files.createDirectories(promptStorageLocation);
            String fileName = UUID.randomUUID() + ".txt";
            Path targetLocation = promptStorageLocation.resolve(fileName).normalize();
            Files.writeString(
                    targetLocation,
                    prompt == null ? "" : prompt,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("提示词保存失败", e);
        }
    }

    //将txt文本加载为String
    public String loadPromptFromTxt(Integer id){
        try {
            String fileName = promptInfoMapper.selectById(id).getName();
            if (fileName == null || fileName.isEmpty()) {
                return null;
            }
            Path basePath = promptStorageLocation.toAbsolutePath().normalize();
            Path filePath = basePath.resolve(fileName).normalize();
            if (!filePath.startsWith(basePath)) {
                throw new RuntimeException("非法文件路径: " + fileName);
            }
            if (!Files.exists(filePath)) {
                throw new RuntimeException("提示词文件不存在: " + fileName);
            }
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("提示词加载失败: 提示词信息id" + id, e);
        }
    }

    //删除txt文件
    public boolean deletePromptTxt(Integer id) {
        try {
            String fileName = promptInfoMapper.selectById(id).getName();
            if (fileName == null || fileName.isEmpty()) {
                return false;
            }
            Path filePath = promptStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
