package cn.net.yunlou.bole.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/** 文件上传安全工具类 包含文件名校验、恶意文件检测等功能 */
@Slf4j
@Component
public class FileSecurityUtils {

    // 私有构造函数，防止实例化
    private FileSecurityUtils() {
        log.debug("FileSecurityUtils initialized");
    }

    // ==================== 文件名安全处理 ====================

    /** 非法字符正则表达式 */
    private static final Pattern ILLEGAL_CHARACTERS =
            Pattern.compile(
                    "[\\\\/:*?\"<>|]" // Windows 非法字符
                    );

    /** 路径遍历模式 */
    private static final Pattern PATH_TRAVERSAL =
            Pattern.compile(
                    "(\\.\\.(\\\\|/))|((\\\\|/)\\.\\.)" // 防止 ../ 或 ..\
                    );

    /** 最大文件名长度 */
    private static final int MAX_FILENAME_LENGTH = 255;

    /**
     * 清理文件名，防止路径遍历和注入攻击
     *
     * @param filename 原始文件名
     * @return 安全的文件名
     */
    public static String sanitizeFilename(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "unnamed_file";
        }

        // 1. 获取基础文件名（去掉路径）
        String baseName = FilenameUtils.getName(filename);

        // 2. 移除非法字符
        String cleanName = ILLEGAL_CHARACTERS.matcher(baseName).replaceAll("_");

        // 3. 防止路径遍历
        if (PATH_TRAVERSAL.matcher(cleanName).find()) {
            log.warn("检测到路径遍历尝试: {}", filename);
            cleanName = cleanName.replaceAll("(\\.\\.(\\\\|/))|((\\\\|/)\\.\\.)", "_");
        }

        // 4. 限制文件名长度
        if (cleanName.length() > MAX_FILENAME_LENGTH) {
            String extension = FilenameUtils.getExtension(cleanName);
            String nameWithoutExt = cleanName.substring(0, cleanName.lastIndexOf('.'));

            // 截断并保留扩展名
            int maxNameLength = MAX_FILENAME_LENGTH - extension.length() - 1;
            if (maxNameLength > 0) {
                cleanName =
                        nameWithoutExt.substring(
                                        0, Math.min(nameWithoutExt.length(), maxNameLength))
                                + "."
                                + extension;
            } else {
                cleanName = cleanName.substring(0, MAX_FILENAME_LENGTH);
            }
        }

        // 5. 如果清理后为空，使用默认名称
        if (StringUtils.isBlank(cleanName)) {
            cleanName = "file_" + System.currentTimeMillis();
        }

        // 6. 添加随机后缀，防止覆盖攻击
        String nameWithoutExt = FilenameUtils.removeExtension(cleanName);
        String extension = FilenameUtils.getExtension(cleanName);
        String randomSuffix = "_" + UUID.randomUUID().toString().substring(0, 8);

        if (StringUtils.isNotBlank(extension)) {
            return nameWithoutExt + randomSuffix + "." + extension;
        } else {
            return cleanName + randomSuffix;
        }
    }

    /**
     * 验证文件名是否安全
     *
     * @param filename 文件名
     * @return 是否安全
     */
    public static boolean isFilenameSafe(String filename) {
        if (StringUtils.isBlank(filename)) {
            return false;
        }

        // 检查非法字符
        if (ILLEGAL_CHARACTERS.matcher(filename).find()) {
            return false;
        }

        // 检查路径遍历
        if (PATH_TRAVERSAL.matcher(filename).find()) {
            return false;
        }

        // 检查文件名长度
        if (filename.length() > MAX_FILENAME_LENGTH) {
            return false;
        }

        // 检查空文件名或只有扩展名
        String baseName = FilenameUtils.getBaseName(filename);
        if (StringUtils.isBlank(baseName) || baseName.equals(".")) {
            return false;
        }

        return true;
    }

    // ==================== 恶意文件检测 ====================

    /** 危险文件扩展名（可执行文件、脚本等） */
    private static final Set<String> DANGEROUS_EXTENSIONS =
            new HashSet<>(
                    Arrays.asList(
                            // 可执行文件
                            "exe",
                            "bat",
                            "cmd",
                            "sh",
                            "bin",
                            "msi",
                            "app",
                            "dmg",
                            // 脚本文件
                            "js",
                            "vbs",
                            "ps1",
                            "php",
                            "jsp",
                            "asp",
                            "aspx",
                            "py",
                            "pl",
                            // 系统文件
                            "dll",
                            "sys",
                            "drv",
                            "ocx",
                            "cpl",
                            // 其他危险格式
                            "jar",
                            "war",
                            "ear",
                            "class",
                            "swf",
                            "wsf",
                            "hta"));

    /** 允许的文件扩展名（白名单） */
    private static final Set<String> ALLOWED_EXTENSIONS =
            new HashSet<>(
                    Arrays.asList(
                            // 图片
                            "jpg",
                            "jpeg",
                            "png",
                            "gif",
                            "bmp",
                            "webp",
                            "svg",
                            "ico",
                            // 文档
                            "pdf",
                            "doc",
                            "docx",
                            "xls",
                            "xlsx",
                            "ppt",
                            "pptx",
                            "txt",
                            "csv",
                            "md",
                            "rtf",
                            "odt",
                            "ods",
                            "odp",
                            // 压缩文件
                            "zip",
                            "rar",
                            "7z",
                            "gz",
                            "tar",
                            // 音频视频
                            "mp3",
                            "mp4",
                            "wav",
                            "avi",
                            "mov",
                            "wmv",
                            "flv",
                            "mkv",
                            // 其他
                            "json",
                            "xml",
                            "html",
                            "htm",
                            "css"));

    /** 文件魔数签名（文件头） key: 文件扩展名, value: 对应的魔数 */
    private static final Map<String, byte[][]> FILE_SIGNATURES = new HashMap<>();

    static {
        // 初始化文件签名
        FILE_SIGNATURES.put(
                "jpg",
                new byte[][] {
                    {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0},
                    {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1},
                    {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE8}
                });

        FILE_SIGNATURES.put(
                "png", new byte[][] {{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}});

        FILE_SIGNATURES.put(
                "gif",
                new byte[][] {
                    {0x47, 0x49, 0x46, 0x38, 0x37, 0x61},
                    {0x47, 0x49, 0x46, 0x38, 0x39, 0x61}
                });

        FILE_SIGNATURES.put("pdf", new byte[][] {{0x25, 0x50, 0x44, 0x46}});

        FILE_SIGNATURES.put(
                "zip",
                new byte[][] {
                    {0x50, 0x4B, 0x03, 0x04},
                    {0x50, 0x4B, 0x05, 0x06},
                    {0x50, 0x4B, 0x07, 0x08}
                });

        FILE_SIGNATURES.put(
                "rar",
                new byte[][] {
                    {0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00},
                    {0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x01, 0x00}
                });

        FILE_SIGNATURES.put(
                "exe",
                new byte[][] {
                    {0x4D, 0x5A} // MZ
                });
    }

    /**
     * 检测是否为恶意文件
     *
     * @param file 上传的文件
     * @return 是否为恶意文件
     */
    public static boolean isMaliciousFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        try {
            // 1. 检查文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isBlank(originalFilename)) {
                log.warn("上传文件没有文件名");
                return true; // 没有文件名的文件可能是恶意的
            }

            String extension = getFileExtension(originalFilename).toLowerCase();

            // 2. 检查是否为危险扩展名
            if (isDangerousExtension(extension)) {
                log.warn("检测到危险文件类型: {}", extension);
                return true;
            }

            // 3. 白名单检查（可选）
            if (!isAllowedExtension(extension)) {
                log.warn("检测到不在白名单中的文件类型: {}", extension);
                return true; // 如果使用白名单模式
            }

            // 4. 检查文件内容（文件头验证）
            if (!isValidFileContent(file, extension)) {
                log.warn("文件内容与扩展名不匹配: {}", originalFilename);
                return true;
            }

            // 5. 检查文件大小异常（例如：0字节或超大文件）
            if (file.getSize() == 0) {
                log.warn("上传文件大小为0字节");
                return true;
            }

            // 6. 检查压缩文件中的恶意内容
            if (isCompressedFile(extension)) {
                if (containsMaliciousContentInArchive(file)) {
                    log.warn("压缩文件中包含恶意内容: {}", originalFilename);
                    return true;
                }
            }

            // 7. 检查文件是否为双重扩展名（例如：evil.jpg.exe）
            if (isDoubleExtension(originalFilename)) {
                log.warn("检测到双重扩展名文件: {}", originalFilename);
                return true;
            }

            return false;

        } catch (Exception e) {
            log.error("恶意文件检测异常", e);
            return true; // 检测异常时，为了安全起见，拒绝文件
        }
    }

    /** 获取文件扩展名 */
    public static String getFileExtension(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /** 检查是否为危险扩展名 */
    private static boolean isDangerousExtension(String extension) {
        return DANGEROUS_EXTENSIONS.contains(extension.toLowerCase());
    }

    /** 检查是否在允许的扩展名列表中 */
    private static boolean isAllowedExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /** 检查文件内容是否与扩展名匹配 */
    private static boolean isValidFileContent(MultipartFile file, String expectedExtension) {
        try (InputStream inputStream = file.getInputStream()) {
            // 读取文件头
            byte[] header = new byte[8];
            int bytesRead = inputStream.read(header, 0, header.length);

            if (bytesRead < header.length) {
                return false; // 文件太小，无法读取完整文件头
            }

            // 检查是否有对应的文件签名
            byte[][] signatures = FILE_SIGNATURES.get(expectedExtension);
            if (signatures == null) {
                // 如果没有预定义的签名，认为验证通过
                return true;
            }

            // 检查是否匹配任一签名
            for (byte[] signature : signatures) {
                if (bytesRead >= signature.length) {
                    boolean match = true;
                    for (int i = 0; i < signature.length; i++) {
                        if (header[i] != signature[i]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return true;
                    }
                }
            }

            return false;

        } catch (IOException e) {
            log.error("读取文件内容失败", e);
            return false;
        }
    }

    /** 检查是否为压缩文件 */
    private static boolean isCompressedFile(String extension) {
        return Arrays.asList("zip", "rar", "7z", "gz", "tar").contains(extension.toLowerCase());
    }

    /** 检查压缩文件中是否包含恶意内容 */
    private static boolean containsMaliciousContentInArchive(MultipartFile file) {
        if (!"zip".equalsIgnoreCase(getFileExtension(file.getOriginalFilename()))) {
            return false; // 只检查zip文件，其他格式需要额外处理
        }

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            int totalEntries = 0;
            long totalSize = 0;

            while ((entry = zis.getNextEntry()) != null) {
                totalEntries++;

                // 1. 检查压缩包中的文件名
                String entryName = entry.getName();
                if (!isFilenameSafe(entryName)) {
                    log.warn("压缩包中包含不安全文件名: {}", entryName);
                    return true;
                }

                // 2. 检查压缩包中的文件扩展名
                String entryExt = getFileExtension(entryName);
                if (isDangerousExtension(entryExt)) {
                    log.warn("压缩包中包含危险文件类型: {}", entryExt);
                    return true;
                }

                // 3. 检查压缩炸弹（过多的文件或过大的解压后大小）
                totalSize += entry.getSize();

                if (totalEntries > 1000) {
                    log.warn("压缩包中包含过多文件: {}", totalEntries);
                    return true; // 压缩炸弹检测：文件数量过多
                }

                if (totalSize > 100 * 1024 * 1024) { // 100MB
                    log.warn("压缩包解压后大小过大: {} bytes", totalSize);
                    return true; // 压缩炸弹检测：解压后大小过大
                }

                // 4. 检查路径遍历
                if (entryName.contains("..")) {
                    log.warn("压缩包中包含路径遍历: {}", entryName);
                    return true;
                }

                // 5. 检查符号链接（如果支持）
                if (entry.isDirectory()) {
                    // 检查目录深度
                    int depth = entryName.split("/").length;
                    if (depth > 10) {
                        log.warn("压缩包中目录深度过深: {}", depth);
                        return true;
                    }
                }

                zis.closeEntry();
            }

            return false;

        } catch (IOException e) {
            log.error("检查压缩文件失败", e);
            return true; // 解析失败，可能是恶意文件
        }
    }

    /** 检查是否为双重扩展名 例如：evil.jpg.exe, document.pdf.exe */
    private static boolean isDoubleExtension(String filename) {
        if (StringUtils.isBlank(filename)) {
            return false;
        }

        String name = filename.toLowerCase();
        int lastDot = name.lastIndexOf('.');
        if (lastDot <= 0) {
            return false;
        }

        // 查找前一个点
        int secondLastDot = name.lastIndexOf('.', lastDot - 1);
        if (secondLastDot > 0) {
            String firstExt = name.substring(secondLastDot + 1, lastDot);
            String secondExt = name.substring(lastDot + 1);

            // 检查是否试图伪装文件类型
            // 例如：jpg.exe, pdf.exe
            if (ALLOWED_EXTENSIONS.contains(firstExt) && DANGEROUS_EXTENSIONS.contains(secondExt)) {
                return true;
            }
        }

        return false;
    }

    // ==================== 高级恶意内容检测 ====================

    /**
     * 检查文件中是否包含恶意脚本
     *
     * @param file 文件
     * @return 是否包含恶意脚本
     */
    public static boolean containsMaliciousScript(MultipartFile file) {
        // 只检查文本文件和HTML文件
        String extension = getFileExtension(file.getOriginalFilename());
        if (!Arrays.asList("txt", "html", "htm", "js", "xml", "json").contains(extension)) {
            return false;
        }

        try (BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null && lineCount < 1000) {
                lineCount++;

                // 检查常见的恶意脚本模式
                if (containsMaliciousPattern(line)) {
                    return true;
                }
            }

            return false;

        } catch (IOException e) {
            log.error("检查文件内容失败", e);
            return false;
        }
    }

    /** 检查是否包含恶意模式 */
    private static boolean containsMaliciousPattern(String line) {
        String lowerLine = line.toLowerCase();

        // XSS攻击模式
        String[] xssPatterns = {
            "<script",
            "javascript:",
            "onload=",
            "onerror=",
            "onclick=",
            "eval(",
            "alert(",
            "document.cookie",
            "window.location",
            "document.write"
        };

        // 命令注入模式
        String[] commandInjectionPatterns = {
            "exec(",
            "system(",
            "Runtime.getRuntime().exec",
            "ProcessBuilder",
            "cmd.exe",
            "/bin/sh",
            "bash -c"
        };

        // SQL注入模式（简化）
        String[] sqlInjectionPatterns = {
            "' or '1'='1", "union select", "drop table", "delete from", "insert into", "update set"
        };

        // 检查各种恶意模式
        for (String pattern : xssPatterns) {
            if (lowerLine.contains(pattern)) {
                return true;
            }
        }

        for (String pattern : commandInjectionPatterns) {
            if (lowerLine.contains(pattern)) {
                return true;
            }
        }

        for (String pattern : sqlInjectionPatterns) {
            if (lowerLine.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    // ==================== 图像文件安全检查 ====================

    /** 检查图像文件是否安全 防止图像中包含恶意代码或超大尺寸 */
    public static boolean isImageSafe(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        if (!Arrays.asList("jpg", "jpeg", "png", "gif", "bmp").contains(extension)) {
            return true; // 不是图片文件，跳过检查
        }

        try {
            // 1. 检查文件大小（限制图片大小）
            long maxImageSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxImageSize) {
                log.warn("图片文件过大: {} bytes", file.getSize());
                return false;
            }

            // 2. 简单的文件头验证（已经在isValidFileContent中检查）
            // 3. 检查是否为畸形图片（尝试读取尺寸信息）

            // 对于PNG和JPEG，可以读取基本的元数据
            if ("png".equals(extension) || "jpg".equals(extension) || "jpeg".equals(extension)) {
                byte[] header = new byte[24];
                try (InputStream is = file.getInputStream()) {
                    int read = is.read(header);
                    if (read < 24) {
                        return false; // 文件太小，不是有效的图片
                    }
                }

                // 简单的PNG验证
                if ("png".equals(extension)) {
                    // PNG文件头：89 50 4E 47 0D 0A 1A 0A
                    if (header[0] != (byte) 0x89
                            || header[1] != 0x50
                            || header[2] != 0x4E
                            || header[3] != 0x47) {
                        return false;
                    }
                }

                // 简单的JPEG验证
                if ("jpg".equals(extension) || "jpeg".equals(extension)) {
                    // JPEG文件头：FF D8 FF
                    if (header[0] != (byte) 0xFF
                            || header[1] != (byte) 0xD8
                            || header[2] != (byte) 0xFF) {
                        return false;
                    }
                }
            }

            return true;

        } catch (IOException e) {
            log.error("检查图片安全失败", e);
            return false;
        }
    }

    // ==================== 其他安全工具方法 ====================

    /**
     * 检查文件路径是否安全
     *
     * @param path 文件路径
     * @param baseDir 基础目录
     * @return 是否安全
     */
    public static boolean isPathSafe(Path path, Path baseDir) {
        try {
            // 规范化和检查路径
            Path normalizedPath = path.normalize().toAbsolutePath();
            Path normalizedBaseDir = baseDir.normalize().toAbsolutePath();

            // 检查路径是否在基础目录内
            return normalizedPath.startsWith(normalizedBaseDir);

        } catch (Exception e) {
            log.error("检查文件路径安全失败", e);
            return false;
        }
    }

    /**
     * 检查文件是否为隐藏文件
     *
     * @param filename 文件名
     * @return 是否为隐藏文件
     */
    public static boolean isHiddenFile(String filename) {
        if (StringUtils.isBlank(filename)) {
            return false;
        }

        // Unix/Linux: 以点开头的文件
        if (filename.startsWith(".")) {
            return true;
        }

        // Windows: 具有隐藏属性（这里通过文件名简单判断）
        String name = filename.toLowerCase();
        return name.contains("thumbs.db")
                || name.contains("desktop.ini")
                || name.startsWith("~$"); // Office临时文件
    }

    public static boolean isImageFile(String filename) {
        String ext = getFileExtension(filename);
        return Arrays.asList("jpg", "jpeg", "png", "gif", "bmp").contains(ext.toLowerCase());
    }

    public static boolean isTextFile(String filename) {
        String ext = getFileExtension(filename);
        return Arrays.asList("txt", "html", "htm", "js", "xml", "json", "csv")
                .contains(ext.toLowerCase());
    }

    /**
     * 生成安全的存储路径
     *
     * @param originalFilename 原始文件名
     * @param userId 用户ID（用于隔离）
     * @param baseDir 基础目录
     * @return 安全的存储路径
     */
    public static Path generateSafePath(String originalFilename, String userId, Path baseDir) {
        // 1. 清理文件名
        String safeFilename = sanitizeFilename(originalFilename);

        // 2. 按用户ID创建子目录
        String userDir = StringUtils.isNotBlank(userId) ? userId : "anonymous";

        // 3. 按日期创建子目录
        String dateDir = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());

        // 4. 构建完整路径
        Path fullPath = baseDir.resolve(userDir).resolve(dateDir).resolve(safeFilename);

        // 5. 确保路径在基础目录内
        if (!isPathSafe(fullPath, baseDir)) {
            throw new SecurityException("生成的文件路径不安全: " + fullPath);
        }

        return fullPath;
    }
}
