package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.handler.IDocumentGeneratorStrategy;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.service.ResumesService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.util.StringUtils;

/** 文档生成策略基类 */
@Getter
@Slf4j
public abstract class AbstractDocumentGeneratorStrategy implements IDocumentGeneratorStrategy {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private final DocumentDirectoryManager directoryManager;

    private final ResumesService resumesService;

    protected AbstractDocumentGeneratorStrategy(
            DocumentDirectoryManager directoryManager, ResumesService resumesService) {
        this.directoryManager = directoryManager;
        this.resumesService = resumesService;
    }

    @Override
    public File generate(Long resumesId) {
        return generate(resumesId, "desktop", null);
    }

    @Override
    public File generate(Long resumesId, String device, String version) {
        // 默认实现，子类需要重写
        throw new UnsupportedOperationException("子类必须实现generate方法");
    }

    /** 获取简历数据并进行验证 */
    protected Resumes getResumesWithValidation(Long resumesId) {
        Resumes resumes = resumesService.getById(resumesId);
        if (resumes == null) {
            throw new ResourceNotFoundException("简历不存在，ID: " + resumesId);
        }

        // 验证必要字段
        if (resumes.getComponents() == null || resumes.getComponents().isEmpty()) {
            log.warn("简历组件为空，简历ID: {}", resumesId);
        }

        return resumes;
    }

    /** 创建临时文件 */
    protected File createTempFile(String prefix, String suffix) throws IOException {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String filename = String.format("%s_%s_%s%s", prefix, timestamp, uuid, suffix);
        Path filePath = directoryManager.getTempFilePath(filename);

        File file = filePath.toFile();
        if (!file.getParentFile().exists()) {
            Files.createDirectories(file.getParentFile().toPath());
        }

        // 创建空文件
        Files.createFile(filePath);

        log.debug("创建临时文件: {}", filePath);
        return file;
    }

    /** 创建输出文件 */
    protected File createOutputFile(
            Long resumesId, DocumentType documentType, String device, String version)
            throws IOException {
        String filename = generateFilename(resumesId, documentType, device, version);
        Path filePath = directoryManager.getOutputFilePath(filename);

        File file = filePath.toFile();
        if (!file.getParentFile().exists()) {
            Files.createDirectories(file.getParentFile().toPath());
        }

        log.debug("创建输出文件: {}", filePath);
        return file;
    }

    /** 生成文件名 */
    protected String generateFilename(
            Long resumesId, DocumentType documentType, String device, String version) {
        // 临时添加调试日志
        String extension = getFileExtension();
        System.out.println("扩展名返回值: " + extension); // 或使用日志框架

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String deviceSuffix = StringUtils.hasText(device) ? "_" + device : "";
        String versionSuffix = StringUtils.hasText(version) ? "_" + version : "";

        // 生成完整文件名
        String fullName =
                String.format(
                        "resume_%d_%s%s%s_%s.%s",
                        resumesId,
                        documentType.name().toLowerCase(),
                        deviceSuffix,
                        versionSuffix,
                        timestamp,
                        extension);

        System.out.println("生成的文件名: " + fullName); // 调试输出
        return fullName;
    }

    /** 获取文件扩展名 */
    protected abstract String getFileExtension();

    /** 验证输出文件 */
    protected boolean validateOutputFile(File file) {
        if (file == null || !file.exists()) {
            log.warn("输出文件不存在或为空");
            return false;
        }

        if (file.length() == 0) {
            log.warn("输出文件大小为0: {}", file.getAbsolutePath());
            return false;
        }

        return true;
    }

    /** 清理临时文件 */
    protected void cleanupTempFile(File file) {
        if (file != null && file.exists()) {
            try {
                if (file.delete()) {
                    log.debug("已清理临时文件: {}", file.getAbsolutePath());
                } else {
                    log.warn("无法删除临时文件: {}", file.getAbsolutePath());
                }
            } catch (Exception e) {
                log.warn("清理临时文件失败: {}", file.getAbsolutePath(), e);
            }
        }
    }

    /** 获取字体文件 */
    protected File getFontFile(String fontName) {
        return directoryManager.getFontFilePath(fontName).toFile();
    }

    /** 检查字体文件是否存在 */
    protected boolean checkFontExists(String fontName) {
        File fontFile = getFontFile(fontName);
        return fontFile.exists() && fontFile.isFile() && fontFile.length() > 0;
    }
}
