package com.hbwl.utils;

import com.hbwl.mapper.VideoChunkMapper;
import com.hbwl.pojo.VideoChunk;
import com.hbwl.pojo.VideoContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class VideoContentUtil {

    @Qualifier("video")
    @Autowired
    private Path videoStorageLocation;

    @Qualifier("chunk")
    @Autowired
    private Path videoChunkStorageLocation;

    @Autowired
    private VideoChunkMapper videoChunkMapper;

    //将文件存储到本地，返回文件名称
    public String storeVideoChunk(MultipartFile file) {
        try {
            //生成文件名
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = "";
            if (originalFileName.contains(".")){
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID() + fileExtension;

            Path targetLocation = videoChunkStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败: " + file.getOriginalFilename(), e);
        }
    }

    //合并分片（默认传参合法）
    //返回合成后的视频文件名
    public void mergeVideoChunk(List<VideoChunk> chunks, VideoContent videoContent) {
        try {
            String videoFileName = videoContent.getName();
            FileOutputStream fos = new FileOutputStream(videoStorageLocation.resolve(videoFileName).toFile());
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            //按分片编号排序
            chunks.sort(Comparator.comparing(
                    chunk -> chunk.getNumber()
            ));
            for (VideoChunk chunk : chunks) {
                try (FileInputStream fis = new FileInputStream(videoChunkStorageLocation.resolve(chunk.getName()).toFile());
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    byte[] buffer = new byte[1024 * 1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                }
                chunk.setCanDeleted(true); //由定时任务来删除
                videoChunkMapper.updateById(chunk);
            }
        } catch (Exception e) {
            throw new RuntimeException("分片合并失败", e);
        }
    }

    //根据url将文件移除
    public boolean removeVideoChunk(String fileName) {
        try {
            Path filePath = videoChunkStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败: " + fileName, e);
        }
    }

    public boolean removeVideo(String fileName){
        try {
            Path filePath = videoStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败: " + fileName, e);
        }
    }

    public ResponseEntity<Resource> loadVideo(String fileName, String rangeHeader){
        try {
            Path filePath = videoStorageLocation.resolve(fileName).normalize();
            // 安全检查：防止路径遍历
            if (!filePath.startsWith(videoStorageLocation)) {
                log.warn("非法路径访问尝试: {}", filePath);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (!Files.exists(filePath)){
                log.warn("文件不存在: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            long fileSize = Files.size(filePath);
            // 如果没有 Range 请求，返回完整文件
            if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
                Resource resource = new UrlResource(filePath.toUri());

                return ResponseEntity.ok()
                        .contentType(detectMediaType(fileName))
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            }

            // 解析 Range 头
            RangeData rangeData = parseRange(rangeHeader, fileSize);

            if (!rangeData.isValid()) {
                // 416 Requested Range Not Satisfiable
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                        .build();
            }

            // 创建 Range 流
            InputStream rangeStream = createRangeInputStream(
                    filePath,
                    rangeData.getStart(),
                    rangeData.getEnd()
            );

            long rangeLength = rangeData.getLength();

            // 创建 Resource 包装
            Resource resource = new InputStreamResource(rangeStream) {
                @Override
                public long contentLength() {
                    return rangeLength;
                }

                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            // 构建响应
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(detectMediaType(fileName));
            headers.setContentLength(rangeLength);
            headers.set(HttpHeaders.CONTENT_RANGE,
                    String.format("bytes %d-%d/%d",
                            rangeData.getStart(),
                            rangeData.getEnd(),
                            fileSize));
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + fileName + "\"");

            log.info("处理视频 Range 请求: {} ({} bytes)", fileName, rangeLength);

            return new ResponseEntity<>(resource, headers, HttpStatus.PARTIAL_CONTENT);
        } catch (IOException e) {
            log.error("处理视频流失败: {}", fileName, e);
            return null;
        }
    }

    /**
     * 创建 Range 输入流
     */
    private InputStream createRangeInputStream(Path filePath, long start, long end) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath.toFile(), "r");
        final long fileLength = randomAccessFile.length();
        final long finalEnd = Math.min(end, fileLength - 1);
        final long finalStart = start;

        return new InputStream() {
            private long position = finalStart;
            private boolean closed = false;

            @Override
            public int read() throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }

                if (position > finalEnd) {
                    return -1;
                }

                randomAccessFile.seek(position);
                int byteRead = randomAccessFile.read();
                if (byteRead != -1) {
                    position++;
                }
                return byteRead;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }

                if (position > finalEnd) {
                    return -1;
                }

                long remaining = finalEnd - position + 1;
                int bytesToRead = (int) Math.min(len, remaining);

                randomAccessFile.seek(position);
                int bytesRead = randomAccessFile.read(b, off, bytesToRead);

                if (bytesRead > 0) {
                    position += bytesRead;
                }
                return bytesRead;
            }

            @Override
            public void close() throws IOException {
                if (!closed) {
                    randomAccessFile.close();
                    closed = true;
                }
            }

            @Override
            public int available() throws IOException {
                return (int) Math.min(finalEnd - position + 1, Integer.MAX_VALUE);
            }
        };
    }

    /**
     * 解析 Range 头
     */
    private RangeData parseRange(String rangeHeader, long fileSize) {
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            return RangeData.invalid();
        }

        String range = rangeHeader.substring(6);
        String[] ranges = range.split("-");

        if (ranges.length < 1 || ranges.length > 2) {
            return RangeData.invalid();
        }

        try {
            long start = Long.parseLong(ranges[0].trim());
            long end;

            if (ranges.length == 1 || ranges[1].trim().isEmpty()) {
                end = fileSize - 1;
            } else {
                end = Long.parseLong(ranges[1].trim());
            }

            // 边界检查
            if (start < 0 || end < start || start >= fileSize) {
                return RangeData.invalid();
            }

            // 确保 end 不超过文件大小
            if (end >= fileSize) {
                end = fileSize - 1;
            }

            return RangeData.valid(start, end, fileSize);

        } catch (NumberFormatException e) {
            log.error("Range 头格式错误: {}", rangeHeader, e);
            return RangeData.invalid();
        }
    }

    /**
     * 检测媒体类型
     */
    private MediaType detectMediaType(String filename) {
        try {
            Path filePath = videoStorageLocation.resolve(filename).normalize();
            String contentType = Files.probeContentType(filePath);

            if (contentType != null) {
                return MediaType.parseMediaType(contentType);
            }
        } catch (IOException e) {
            log.warn("无法探测文件类型: {}", filename, e);
        }

        // 根据文件扩展名判断
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".mp4")) {
            return MediaType.parseMediaType("video/mp4");
        } else if (lowerFilename.endsWith(".webm")) {
            return MediaType.parseMediaType("video/webm");
        } else if (lowerFilename.endsWith(".avi")) {
            return MediaType.parseMediaType("video/x-msvideo");
        } else if (lowerFilename.endsWith(".mov")) {
            return MediaType.parseMediaType("video/quicktime");
        } else if (lowerFilename.endsWith(".mkv")) {
            return MediaType.parseMediaType("video/x-matroska");
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    /**
     * Range 数据封装
     */
    @Data
    @AllArgsConstructor(staticName = "of")
    private static class RangeData {
        private long start;
        private long end;
        private long fileSize;
        private boolean valid;

        public static RangeData invalid() {
            return new RangeData(0, 0, 0, false);
        }

        public static RangeData valid(long start, long end, long fileSize) {
            return new RangeData(start, end, fileSize, true);
        }

        public long getLength() {
            return isValid() ? (end - start + 1) : 0;
        }
    }
}
