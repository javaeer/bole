package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.config.DocumentGeneratorProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 文档生成目录管理器 */
@Slf4j
@Component
public class DocumentDirectoryManager {

    @Getter private final Path tempPath;

    @Getter private final Path fontPath;

    @Getter private final Path outputPath;

    private final DocumentGeneratorProperties properties;

    public DocumentDirectoryManager(DocumentGeneratorProperties properties) {
        this.properties = properties;

        // 使用配置的目录，如果没有配置则使用默认值
        String tempDir =
                StringUtils.hasText(properties.getTempDir())
                        ? properties.getTempDir()
                        : System.getProperty("java.io.tmpdir") + "/resume-generator/temp";

        String fontDir =
                StringUtils.hasText(properties.getFontDir())
                        ? properties.getFontDir()
                        : System.getProperty("java.io.tmpdir") + "/resume-generator/fonts";

        String outputDir =
                StringUtils.hasText(properties.getOutputDir())
                        ? properties.getOutputDir()
                        : System.getProperty("java.io.tmpdir") + "/resume-generator/output";

        this.tempPath = Paths.get(tempDir).toAbsolutePath();
        this.fontPath = Paths.get(fontDir).toAbsolutePath();
        this.outputPath = Paths.get(outputDir).toAbsolutePath();
    }

    @PostConstruct
    public void init() {
        try {
            // 创建所有必要的目录
            createDirectoryWithPermissions(tempPath, "临时文件");
            createDirectoryWithPermissions(fontPath, "字体文件");
            createDirectoryWithPermissions(outputPath, "输出文件");

            log.info("文档生成目录初始化完成");
            log.info("临时目录: {}", tempPath);
            log.info("字体目录: {}", fontPath);
            log.info("输出目录: {}", outputPath);

        } catch (Exception e) {
            log.error("文档生成目录初始化失败", e);
            throw new RuntimeException("文档生成目录初始化失败", e);
        }
    }

    /** 创建目录并设置权限 */
    private void createDirectoryWithPermissions(Path path, String description) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("创建{}目录: {}", description, path);
        } else {
            log.debug("{}目录已存在: {}", description, path);
        }

        // 设置目录权限
        setDirectoryPermissions(path);
    }

    /** 设置目录权限 */
    private void setDirectoryPermissions(Path path) throws IOException {
        if (!isWindows()) {
            try {
                Set<PosixFilePermission> permissions = new HashSet<>();
                permissions.add(PosixFilePermission.OWNER_READ);
                permissions.add(PosixFilePermission.OWNER_WRITE);
                permissions.add(PosixFilePermission.OWNER_EXECUTE);
                permissions.add(PosixFilePermission.GROUP_READ);
                permissions.add(PosixFilePermission.GROUP_EXECUTE);
                permissions.add(PosixFilePermission.OTHERS_READ);
                permissions.add(PosixFilePermission.OTHERS_EXECUTE);

                Files.setPosixFilePermissions(path, permissions);
            } catch (UnsupportedOperationException e) {
                log.debug("不支持POSIX文件权限，跳过权限设置");
            }
        }
    }

    /** 获取临时文件路径 */
    public Path getTempFilePath(String filename) {
        return tempPath.resolve(filename);
    }

    /** 获取输出文件路径 */
    public Path getOutputFilePath(String filename) {
        return outputPath.resolve(filename);
    }

    /** 获取字体文件路径 */
    public Path getFontFilePath(String fontName) {
        return fontPath.resolve(fontName);
    }

    /** 检查目录是否可写 */
    public boolean checkDirectoryWritable() {
        try {
            Path testFile = tempPath.resolve(".write_test");
            Files.write(testFile, "test".getBytes());
            Files.delete(testFile);
            return true;
        } catch (Exception e) {
            log.error("目录不可写: {}", tempPath, e);
            return false;
        }
    }

    /** 获取临时目录大小（字节） */
    public long getTempDirectorySize() throws IOException {
        return Files.walk(tempPath)
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
    }

    /** 清理临时目录 */
    public void cleanupTempDirectory() throws IOException {
        Files.walk(tempPath)
                .filter(p -> !p.equals(tempPath))
                .forEach(
                        p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                log.warn("无法删除文件: {}", p, e);
                            }
                        });
        log.info("临时目录已清理: {}", tempPath);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
