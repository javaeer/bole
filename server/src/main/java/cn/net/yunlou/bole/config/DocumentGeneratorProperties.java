package cn.net.yunlou.bole.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "document.generator")
public class DocumentGeneratorProperties {

    /**
     * 临时文件存储目录
     */
    private String tempDir;

    /**
     * 字体文件目录
     */
    private String fontDir;

    /**
     * 输出目录（最终文件存储目录）
     */
    private String outputDir;

    /**
     * 文件保留时间（小时），默认24小时
     */
    private int fileRetentionHours = 24;

    /**
     * 是否启用缓存，默认true
     */
    private boolean cacheEnabled = true;

    /**
     * 是否自动清理临时文件，默认true
     */
    private boolean autoCleanup = true;

    /**
     * 清理时间（cron表达式），默认每天凌晨2点
     */
    private String cleanupCron = "0 0 2 * * ?";

    /**
     * 默认设备类型，默认desktop
     */
    private String defaultDevice = "desktop";

    /**
     * 是否启用HTML压缩，默认true
     */
    private boolean htmlCompression = true;

    /**
     * 是否启用版本控制，默认true
     */
    private boolean versionControl = true;

    /**
     * 最大并发生成数量，默认10
     */
    private int maxConcurrentGenerations = 10;

    /**
     * 生成超时时间（秒），默认60秒
     */
    private int generationTimeoutSeconds = 60;
}