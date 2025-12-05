package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.common.utils.FileHashUtils;
import cn.net.yunlou.bole.common.utils.FileSecurityUtils;
import cn.net.yunlou.bole.entity.File;
import cn.net.yunlou.bole.mapper.FileMapper;
import cn.net.yunlou.bole.model.FileCreate;
import cn.net.yunlou.bole.model.FileEdit;
import cn.net.yunlou.bole.model.FileQuery;
import cn.net.yunlou.bole.model.FileView;
import cn.net.yunlou.bole.service.FileService;
import cn.net.yunlou.bole.service.StorageService;
import cn.net.yunlou.bole.struct.FileStructMapper;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileName: FileServiceImpl Description: Created By laughtiger Created At 2025/12/4 01:27 Modified
 * By Modified At
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl
        extends BaseService<
                FileMapper, File, FileCreate, FileView, FileEdit, FileQuery, FileStructMapper>
        implements FileService {

    private final StorageService storageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File uploadFile(MultipartFile file) {
        // 1. 基础验证
        if (file == null || file.isEmpty()) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_BLANK, "上传文件不能为空");
        }

        // 2. 文件名安全处理
        String originalFilename = file.getOriginalFilename();
        String safeFilename = FileSecurityUtils.sanitizeFilename(originalFilename);
        log.info("文件上传: 原始文件名={}, 安全文件名={}", originalFilename, safeFilename);

        // 3. 恶意文件检测
        if (FileSecurityUtils.isMaliciousFile(file)) {
            log.warn("检测到潜在的安全威胁，文件: {}", originalFilename);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "文件可能包含恶意内容");
        }

        // 4. 隐藏文件检查
        if (FileSecurityUtils.isHiddenFile(safeFilename)) {
            log.warn("检测到隐藏文件: {}", safeFilename);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "不允许上传隐藏文件");
        }

        // 5. 图像文件额外检查
        if (FileSecurityUtils.isImageSafe(file)) {
            if (!FileSecurityUtils.isImageSafe(file)) {
                log.warn("图片文件安全检查失败: {}", safeFilename);
                throw new BusinessException(
                        BusinessStatus.REQUEST_PARAM_ILLEGAL, "图片文件可能已损坏或包含恶意内容");
            }
        }

        // 6. 脚本内容检查（对于文本文件）
        if (FileSecurityUtils.isTextFile(safeFilename)) {
            if (FileSecurityUtils.containsMaliciousScript(file)) {
                log.warn("检测到文件中的恶意脚本: {}", safeFilename);
                throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "文件中包含恶意脚本");
            }
        }

        // 7. 文件去重检查
        String fileHash = FileHashUtils.calculateFileHash(file);
        File existingFile = getByFileKey(fileHash);
        if (existingFile != null) {
            log.info("检测到重复文件，使用已有文件: {}", existingFile.getAccessUrl());
            return existingFile;
        }

        File uploaded = storageService.upload(file);

        // 5. 保存文件记录到数据库（如果需要）
        save(uploaded);

        return uploaded;
    }

    @Override
    public List<File> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        // 限制一次上传的文件数量
        if (files.size() > 10) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "一次最多上传10个文件");
        }

        // 检查总文件大小
        long totalSize = files.stream().mapToLong(MultipartFile::getSize).sum();

        if (totalSize > 100 * 1024 * 1024) { // 100MB
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "总文件大小不能超过100MB");
        }

        List<File> results = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                File result = uploadFile(file);
                results.add(result);
            } catch (BusinessException e) {
                log.warn("文件安全检测失败，跳过: {}", file.getOriginalFilename(), e);
            } catch (Exception e) {
                log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            }
        }

        return List.of();
    }

    @Override
    public InputStream downloadFile(String filePath, String storageType) {
        return storageService.download(filePath, storageType);
    }

    @Override
    public boolean deleteFile(String filePath, String storageType) {
        return false;
    }

    @Override
    public void switchStorageType(String newStorageType) {}

    @Override
    public List<StorageType> getAvailableStorageTypes() {
        return Lists.newArrayList();
    }

    @Override
    public File getByFileKey(String fileKey) {
        File entity = File.builder().fileKey(fileKey).build();
        /*File entity = new File();
        entity.setFileKey(fileKey);*/
        return baseMapper.selectOne(getBaseQueryWrapper(entity));
    }
}
