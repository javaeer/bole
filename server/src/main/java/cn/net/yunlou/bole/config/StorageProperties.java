package cn.net.yunlou.bole.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

/** 存储通用配置属性 */
@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /** 存储类型，默认本地存储 */
    private String type = "local";

    /** 最大文件大小，默认10MB */
    private DataSize maxFileSize = DataSize.ofMegabytes(10);

    /** 允许的文件扩展名 */
    private Set<String> allowedExtensions =
            new HashSet<>(
                    Arrays.asList(
                            "jpg", "jpeg", "png", "gif", "bmp", "pdf", "doc", "docx", "xls", "xlsx",
                            "txt", "zip", "rar"));

    /** 是否启用文件压缩 */
    private boolean enableCompression = false;

    /** 是否启用图片水印 */
    private boolean enableWatermark = false;
}
