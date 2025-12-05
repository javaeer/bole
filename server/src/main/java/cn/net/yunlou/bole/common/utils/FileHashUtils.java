package cn.net.yunlou.bole.common.utils;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileName: FileHashUtils Description: Created By laughtiger Created At 2025/12/4 01:06 Modified By
 * Modified At
 */
@Slf4j
@Component
public class FileHashUtils {

    public FileHashUtils() {
        log.debug("FileHashUtils initialized");
    }

    /**
     * 计算文件哈希值（用于去重和验证）
     *
     * @param file 文件
     * @return MD5哈希值
     */
    public static String calculateFileHash(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;

            while ((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }

            byte[] hashBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            log.error("计算文件哈希失败", e);
            throw new RuntimeException("计算文件哈希失败", e);
        }
    }

    /** 计算带文件名的复合哈希（防止不同文件同名冲突） */
    public static String calculateCompositeHash(MultipartFile file) {
        try {
            // 取前32位
            String contentHash = calculateFileHash(file);
            String filename = file.getOriginalFilename();
            long size = file.getSize();

            // 组合内容哈希、文件名和大小生成最终标识
            String composite = contentHash + "|" + filename + "|" + size;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(composite.getBytes());
            return HexFormat.of().formatHex(hashBytes).substring(0, 32);
        } catch (Exception e) {
            log.error("计算带文件名的复合哈希", e);
            throw new RuntimeException("计算带文件名的复合哈希", e);
        }
    }
}
