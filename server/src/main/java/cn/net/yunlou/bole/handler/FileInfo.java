package cn.net.yunlou.bole.handler;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileInfo {
    private String fileName;
    private String filePath;
    private long fileSize;
    private String contentType;
    private LocalDateTime lastModified;
    private String etag;
}