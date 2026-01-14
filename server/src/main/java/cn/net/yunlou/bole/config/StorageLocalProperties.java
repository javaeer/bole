package cn.net.yunlou.bole.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FileName: LocalStorageProperties Description: Created By laughtiger Created At 2025/12/4 13:14
 * Modified By Modified At
 */
@Data
@ConfigurationProperties(prefix = "storage.local")
public class StorageLocalProperties {

    private String basePath;

    private String urlPrefix;
}
