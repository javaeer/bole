package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.config.StorageProperties;
import cn.net.yunlou.bole.entity.File;
import cn.net.yunlou.bole.handler.IStorage;
import cn.net.yunlou.bole.handler.StorageStrategyRegistry;
import cn.net.yunlou.bole.service.StorageService;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageStrategyRegistry registry;
    private final StorageProperties properties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File upload(MultipartFile file) {
        // 1. 参数校验
        validateFile(file);

        // 2. 安全检查
        performSecurityCheck(file);

        // 3. 获取存储策略
        String storageType = properties.getType();
        IStorage strategy = registry.getStrategy(storageType);

        // 4. 存储文件
        File storedFile = strategy.store(file);

        // 5. 保存文件记录到数据库（如果需要）
        // fileService.save(storedFile);

        log.info(
                "文件上传成功: {}, 存储类型: {}, 访问地址: {}",
                storedFile.getOriginalFilename(),
                storageType,
                storedFile.getAccessUrl());

        return storedFile;
    }

    @Override
    public InputStream download(String filePath, String storageType) {
        IStorage strategy = registry.getStrategy(storageType);
        return strategy.download(filePath);
    }

    @Override
    public String getFileUrl(String fileName, String bucketOrPath) {
        return "";
    }

    @Override
    public boolean delete(String filePath, String storageType) {
        IStorage strategy = registry.getStrategy(storageType);
        return strategy.delete(filePath);
    }

    @Override
    public void switchStorageType(String newStorageType) {
        if (!registry.supports(newStorageType)) {
            throw new UnsupportedOperationException("不支持的存储类型: " + newStorageType);
        }

        // 更新配置
        properties.setType(newStorageType);
        log.info("已切换存储类型为: {}", newStorageType);
    }

    @Override
    public List<StorageType> getAvailableStorageTypes() {
        return registry.getAvailableTypes();
    }

    // 私有辅助方法
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_BLANK, "上传文件不能为空");
        }

        // 文件大小校验
        if (file.getSize() > properties.getMaxFileSize().toBytes()) {
            throw new BusinessException(
                    BusinessStatus.REQUEST_PARAM_ILLEGAL,
                    String.format("文件大小超过限制，最大允许: %s", properties.getMaxFileSize()));
        }

        // 文件类型校验
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!properties.getAllowedExtensions().contains(extension.toLowerCase())) {
            throw new BusinessException(
                    BusinessStatus.REQUEST_PARAM_ILLEGAL,
                    String.format(
                            "不支持的文件类型: %s，允许的类型: %s",
                            extension, properties.getAllowedExtensions()));
        }
    }

    private void performSecurityCheck(MultipartFile file) {
        // 检查文件是否包含恶意内容
        String originalFilename = file.getOriginalFilename();

        // 1. 检查文件名是否安全
        if (originalFilename.contains("..") || originalFilename.contains("/")) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "文件名包含非法字符");
        }

        // 2. 检查文件内容（简化版）
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "无法识别文件类型");
        }
    }
}
