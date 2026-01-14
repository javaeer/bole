package cn.net.yunlou.bole.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FileName: MinioStorageProperties Description: Created By laughtiger Created At 2025/12/4 15:24
 * Modified By Modified At
 */
@Data
@ConfigurationProperties(prefix = "storage.minio")
public class StorageMinioProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String region;

    private boolean secure = false;

    /** 连接超时时间（毫秒） */
    private int connectTimeout = 30000;

    /** 读取超时时间（毫秒） */
    private int readTimeout = 30000;
}
