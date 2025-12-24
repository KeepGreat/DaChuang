package com.hbwl.utils;

import com.hbwl.pojo.FileContent;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentSource;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileContentUtil {

    @Autowired
    private Path fileStorageLocation;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    //将文件存储到本地，返回文件名称
    public String storeFile(MultipartFile file) {
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
    public Resource loadFile(String fileName) {
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
    public boolean removeFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败: " + fileName, e);
        }
    }

    //将文件嵌入向量数据库
    public void embedVector(FileContent fileContent, MultipartFile file){
        //加载文件为document
        DocumentSource documentSource = new DocumentSource() {
            @Override
            public InputStream inputStream() throws IOException {
                return file.getInputStream();
            }

            @Override
            public Metadata metadata() {
                return (new Metadata()).put("file_name", fileContent.getName());
            }
        };
        Document document = DocumentLoader.load(documentSource, new ApacheTikaDocumentParser());

        //将document分割为TextSegment
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(500, 100);
        List<TextSegment> textSegments = splitter.split(document);

        //将TextSegment转换为向量存储
        List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();
        embeddingStore.addAll(embeddings, textSegments);
    }

    //将文件从向量数据库中移除
    public void removeVector(FileContent fileContent){
        Filter filter = new IsEqualTo("file_name", fileContent.getName());
        embeddingStore.removeAll(filter);
    }
}
