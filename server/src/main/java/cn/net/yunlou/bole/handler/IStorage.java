package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.entity.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/** 策略 接口 */
public interface IStorage {

    /** 获取存储类型 */
    StorageType getType();

    /**
     * 存储文件
     *
     * @param file 上传的文件
     * @return 存储后的文件信息
     */
    File store(MultipartFile file);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean delete(String filePath);

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getAccessUrl(String filePath);

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件流
     */
    InputStream download(String filePath);

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean exists(String filePath);

    // ========== 默认方法 ==========

    /** 生成唯一文件名（默认实现） */
    default String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + (StringUtils.hasText(extension) ? "." + extension : "");
    }

    /** 生成当前日期格式路径+文件名的存储地址（默认实现） */
    default String generateDatePathFileName(String uniqueFilename) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formatted = LocalDateTime.now().format(formatter);
        return String.format("%s/%s", formatted, uniqueFilename);
    }

    /** 安全的文件路径构建（防止路径遍历） */
    default String safePathCombine(String basePath, String... subPaths) {
        Path path = Paths.get(basePath, subPaths);
        Path normalized = path.normalize();

        // 安全检查：确保结果路径仍在basePath下
        if (!normalized.startsWith(Paths.get(basePath).normalize())) {
            throw new SecurityException("非法路径: " + path);
        }

        return normalized.toString();
    }

    /** 验证文件大小（默认实现） */
    default void validateFileSize(MultipartFile file, long maxSizeBytes) {
        if (file.getSize() > maxSizeBytes) {
            throw new RuntimeException(
                    String.format("文件大小超过限制: %d > %d", file.getSize(), maxSizeBytes));
        }
    }

    /** 获取文件扩展名（工具性方法） */
    default String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex > 0) ? filename.substring(dotIndex + 1) : "";
    }

    /** 验证文件类型（白名单方式） */
    default boolean isAllowedExtension(String filename, Set<String> allowedExtensions) {
        String extension = getFileExtension(filename).toLowerCase();
        return allowedExtensions.contains(extension);
    }
}
