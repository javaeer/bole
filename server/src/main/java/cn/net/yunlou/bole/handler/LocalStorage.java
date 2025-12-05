package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.common.utils.FileHashUtils;
import cn.net.yunlou.bole.config.StorageLocalProperties;
import cn.net.yunlou.bole.entity.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
@ConditionalOnProperty(
        prefix = "storage",
        name = "type",
        havingValue = "local",
        matchIfMissing = true)
public class LocalStorage implements IStorage {

    private final StorageLocalProperties properties;
    private final Path storagePath;

    @Autowired
    public LocalStorage(StorageLocalProperties properties) {
        this.properties = properties;

        // 初始化存储目录
        this.storagePath = Paths.get(properties.getBasePath()).toAbsolutePath();
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
            // 生成唯一文件名
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String fileName = UUID.randomUUID() + "." + extension;
            Path targetPath = storagePath.resolve(fileName);

            String fileKey = FileHashUtils.calculateFileHash(multipartFile);

            // 保存文件
            multipartFile.transferTo(targetPath.toFile());

            // 构建文件信息对象

            return File.builder()
                    .fileName(fileName)
                    .fileKey(fileKey)
                    .originalFilename(originalFilename)
                    .storagePath(targetPath.toString())
                    .fileSizeBytes(multipartFile.getSize())
                    // .contentType(multipartFile.getContentType())
                    .storageType(StorageType.LOCAL.getValue())
                    .accessUrl(getAccessUrl(fileName))
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
    public String getAccessUrl(String fileName) {
        // 构建访问URL
        String urlPrefix = properties.getUrlPrefix();
        if (StringUtils.isBlank(urlPrefix)) {
            urlPrefix = "/files/";
        }
        return urlPrefix + fileName;
    }

    @Override
    public boolean delete(String filePath) {
        return false;
    }

    @Override
    public InputStream download(String filePath) {
        return null;
    }

    @Override
    public boolean exists(String filePath) {
        return false;
    }

    @Override
    public FileInfo getFileInfo(String filePath) {
        return null;
    }
}
