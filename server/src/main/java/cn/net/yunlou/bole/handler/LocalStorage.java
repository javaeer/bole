package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.common.utils.FileHashUtils;
import cn.net.yunlou.bole.config.AppConfigProperties;
import cn.net.yunlou.bole.config.StorageLocalProperties;
import cn.net.yunlou.bole.model.entity.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
@ConditionalOnProperty(
        prefix = "storage",
        name = "type",
        havingValue = "local",
        matchIfMissing = true)
public class LocalStorage implements IStorage {

    private final StorageLocalProperties storageLocalProperties;
    private final AppConfigProperties appConfigProperties;
    private final Path storagePath;

    @Autowired
    public LocalStorage(
            StorageLocalProperties storageLocalProperties,
            AppConfigProperties appConfigProperties) {
        this.storageLocalProperties = storageLocalProperties;
        this.appConfigProperties = appConfigProperties;

        // 初始化存储目录
        this.storagePath = Paths.get(storageLocalProperties.getBasePath()).toAbsolutePath();
        try {
            Files.createDirectories(storagePath);
            log.info("本地存储策略初始化完成，存储路径: {}", storagePath);
        } catch (IOException e) {
            throw new BusinessException(
                    BusinessStatus.REQUEST_PARAM_ILLEGAL, "创建存储目录失败: " + storagePath);
        }
    }

    @Override
    public StorageType getType() {
        return StorageType.LOCAL;
    }

    @Override
    public File store(MultipartFile multipartFile) {
        try {
            // 计算唯一标识
            String fileKey = FileHashUtils.calculateFileHash(multipartFile);

            // 1. 生成唯一文件名（防止覆盖）
            String originalFilename = multipartFile.getOriginalFilename();
            String uniqueFilename = generateUniqueFilename(originalFilename);

            // 2. 按日期生成存储路径
            String datePathFileName = generateDatePathFileName(uniqueFilename);

            Path targetPath = storagePath.resolve(datePathFileName);

            // 3. 确保目录存在
            Files.createDirectories(targetPath.getParent());

            // 保存文件
            multipartFile.transferTo(targetPath.toFile());

            File file = new File();
            file.setFileKey(fileKey);
            file.setFileName(uniqueFilename);
            file.setOriginalFilename(originalFilename);
            file.setStoragePath(targetPath.toString());
            file.setFileSizeBytes(multipartFile.getSize());
            file.setContentType(multipartFile.getContentType());
            file.setFileKey(fileKey);
            file.setFileKey(fileKey);
            // 构建文件信息对象
            return File.builder()
                    .fileName(uniqueFilename)
                    .fileKey(fileKey)
                    .originalFilename(originalFilename)
                    .storagePath(targetPath.toString())
                    .fileSizeBytes(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .storageType(StorageType.LOCAL.getValue())
                    .accessUrl(getAccessUrl(datePathFileName))
                    .build();

            /*File file = new File();
            file.setFileName(fileName);
            file.setFileKey(fileKey);
            file.setOriginalFilename(originalFilename);
            file.setStoragePath(targetPath.toString());
            file.setFileSizeBytes(multipartFile.getSize());
            //file.setContentType(multipartFile.getContentType());
            file.setStorageType(StorageType.LOCAL.getValue());
            file.setAccessUrl(getAccessUrl(fileName));
            return file;*/

        } catch (Exception e) {
            log.error("本地文件上传失败", e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "文件上传失败");
        }
    }

    @Override
    public boolean delete(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return false;
        }

        try {
            Path path = Paths.get(storageLocalProperties.getBasePath(), filePath).normalize();

            // 安全检查：确保路径在根目录内
            if (!path.startsWith(Paths.get(storageLocalProperties.getBasePath()).normalize())) {
                throw new SecurityException("非法文件路径: " + filePath);
            }

            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new RuntimeException("安全异常: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAccessUrl(String filePath) {

        String baseUrl =
                appConfigProperties.getServer().getDomain() + storageLocalProperties.getUrlPrefix();

        if (!StringUtils.hasText(filePath)) {
            return null;
        }

        // 构建完整的访问URL
        return baseUrl + "/" + filePath.replace("\\", "/");
    }

    @Override
    public InputStream download(String filePath) {
        try {
            Path path = Paths.get(storageLocalProperties.getBasePath(), filePath).normalize();

            // 安全检查
            if (!path.startsWith(Paths.get(storageLocalProperties.getBasePath()).normalize())) {
                throw new SecurityException("非法文件路径: " + filePath);
            }

            if (!Files.exists(path)) {
                throw new FileNotFoundException("文件不存在: " + filePath);
            }

            return Files.newInputStream(path);

        } catch (IOException e) {
            throw new RuntimeException("文件读取失败: " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new RuntimeException("安全异常: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return false;
        }

        Path path = Paths.get(storageLocalProperties.getBasePath(), filePath).normalize();

        // 安全检查
        if (!path.startsWith(Paths.get(storageLocalProperties.getBasePath()).normalize())) {
            return false;
        }

        return Files.exists(path) && Files.isRegularFile(path);
    }
}
