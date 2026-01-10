package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.zip.CRC32;

/**
 * FileName: FileHashUtils Description: Created By laughtiger Created At 2025/12/4 01:06 Modified By
 * Modified At
 */
@Slf4j
@Component
public class FileHashUtils {

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

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
            byte[] buffer = new byte[131072];
            int read;

            while ((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }

            return toHexString(md.digest());

        } catch (Exception e) {
            log.error("计算文件哈希失败", e);
            throw new RuntimeException("计算文件哈希失败", e);
        }
    }

    /**
     * 计算带文件名的复合哈希（防止不同文件同名冲突）
     */
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

    public static String calculateHash(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[131072]; // 128KB 更大缓冲区提升速度
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, read);
            }
            return toHexString(md5.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("计算文件哈希失败", e);
            throw new RuntimeException("计算文件哈希失败", e);
        }
    }


    /**
     * 完整性校验
     */
    public static String calculateHashCRC32(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        CRC32 crc = new CRC32();

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[131072];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                crc.update(buffer, 0, bytesRead);
            }

            return Long.toHexString(crc.getValue());

        } catch (IOException e) {
            return null;
        }
    }

    // 优化的16进制转换
    private static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


}
