package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.common.utils.FileHashUtils;
import cn.net.yunlou.bole.config.StorageMinioProperties;
import cn.net.yunlou.bole.entity.File;
import io.minio.*;
import io.minio.http.Method;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
@ConditionalOnClass(name = "io.minio.MinioClient")
@ConditionalOnProperty(
        prefix = "storage",
        name = "type",
        havingValue = "minio",
        matchIfMissing = false)
public class MinioStorage implements IStorage {

    private final StorageMinioProperties properties;
    private final MinioClient minioClient;

    @Autowired
    public MinioStorage(StorageMinioProperties properties) {
        this.properties = properties;

        // 创建MinIO客户端
        this.minioClient =
                MinioClient.builder()
                        .endpoint(properties.getEndpoint())
                        .credentials(properties.getAccessKey(), properties.getSecretKey())
                        .region(properties.getRegion())
                        .build();

        log.info("MinIO存储策略初始化完成，endpoint: {}", properties.getEndpoint());

        // 初始化时确保bucket存在
        ensureBucketExists(properties.getBucketName());
    }

    @Override
    public StorageType getType() {
        return StorageType.MINIO;
    }

    @Override
    public File store(MultipartFile multipartFile) {
        try {
            // 生成唯一文件名
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String fileName = UUID.randomUUID().toString() + "." + extension;
            String objectName = getObjectPath(fileName);

            String fileKey = FileHashUtils.calculateFileHash(multipartFile);

            // 上传到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(objectName)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build());

            log.info("文件上传成功: {} -> {}", originalFilename, objectName);

            return File.builder()
                    .fileName(fileName)
                    .fileKey(fileKey)
                    .originalFilename(originalFilename)
                    .storagePath(objectName)
                    .fileSizeBytes(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .storageType(StorageType.MINIO.getValue())
                    .accessUrl(getAccessUrl(objectName))
                    .build();

        } catch (Exception e) {
            log.error("MinIO文件上传失败", e);
            throw new BusinessException(BusinessStatus.UNSUPPORTED_CONVERT_FILE_TYPE, "文件上传失败");
        }
    }

    /**
     * 获取对象存储路径
     */
    private String getObjectPath(String fileName) {
        // 可以根据日期等生成路径，例如: 2024/01/15/uuid.ext
        String datePath = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
        return String.format("%s/%s", datePath, fileName);
    }

    @Override
    public boolean delete(String filePath) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(filePath)
                    .build());
            log.info("文件删除成功: {}", filePath);
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public String getAccessUrl(String filePath) {
        try {
            // 生成预签名URL，默认7天有效期
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(properties.getBucketName())
                            .object(filePath)
                            .expiry(7 * 24 * 60 * 60) // 7天
                            .build());
        } catch (Exception e) {
            log.error("生成MinIO访问URL失败", e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "生成访问URL失败");
        }
    }

    /**
     * 获取访问URL（可自定义过期时间）
     */
    public String getAccessUrl(String filePath, int duration, TimeUnit timeUnit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(properties.getBucketName())
                            .object(filePath)
                            .expiry(duration, timeUnit)
                            .build());
        } catch (Exception e) {
            log.error("生成MinIO访问URL失败", e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "生成访问URL失败");
        }
    }

    @Override
    public InputStream download(String filePath) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(filePath)
                    .build());
        } catch (Exception e) {
            log.error("下载文件失败: {}", filePath, e);
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "文件下载失败");
        }
    }

    /**
     * 下载文件为字节数组
     */
    public ResponseEntity<byte[]> downloadAsBytes(String filePath) {
        try (InputStream inputStream = download(filePath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            IOUtils.copy(inputStream, outputStream);
            byte[] bytes = outputStream.toByteArray();

            // 获取原始文件名
            String originalFilename = StringUtils.substringAfterLast(filePath, "/");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.name()) + "\"");
            headers.setContentLength(bytes.length);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setAccessControlExposeHeaders(List.of("*"));

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("下载文件失败: {}", filePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public boolean exists(String filePath) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(filePath)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            // 文件不存在
            return false;
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public FileInfo getFileInfo(String filePath) {
        try {
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(filePath)
                    .build());

            return FileInfo.builder()
                    .fileName(StringUtils.substringAfterLast(filePath, "/"))
                    .filePath(filePath)
                    .fileSize(statObject.size())
                    .contentType(statObject.contentType())
                    //.lastModified(statObject.lastModified())
                    .etag(statObject.etag())
                    .build();
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", filePath, e);
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "文件不存在");
        }
    }

    /**
     * 检查bucket是否存在，不存在则创建
     */
    public boolean ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Bucket '{}' 创建成功", bucketName);
            }
            return true;
        } catch (Exception e) {
            log.error("创建或检查Bucket失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 创建存储bucket
     */
    public boolean createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Bucket '{}' 创建成功", bucketName);
            return true;
        } catch (Exception e) {
            log.error("创建Bucket失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 删除存储bucket
     */
    public boolean deleteBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Bucket '{}' 删除成功", bucketName);
            return true;
        } catch (Exception e) {
            log.error("删除Bucket失败: {}", bucketName, e);
            return false;
        }
    }

    /**
     * 获取上传临时签名（用于前端直传）
     */
    public Map<String, String> generatePresignedPostFormData(String fileName, ZonedDateTime expirationTime) {
        try {
            PostPolicy postPolicy = new PostPolicy(properties.getBucketName(), expirationTime);
            postPolicy.addEqualsCondition("key", fileName);

            Map<String, String> formData = minioClient.getPresignedPostFormData(postPolicy);

            // 转换key格式并添加host
            Map<String, String> result = formData.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().replace("-", ""),
                            Map.Entry::getValue
                    ));

            result.put("host", properties.getEndpoint() + "/" + properties.getBucketName());
            return result;
        } catch (Exception e) {
            log.error("生成上传临时签名失败: {}", fileName, e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "生成上传签名失败");
        }
    }

    /**
     * 生成预签名URL（用于上传或下载）
     */
    public String generatePresignedUrl(String filePath, Method method, int duration, TimeUnit timeUnit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(properties.getBucketName())
                            .object(filePath)
                            .expiry(duration, timeUnit)
                            .build());
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", filePath, e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "生成预签名URL失败");
        }
    }

    /**
     * 上传文件（直接上传方式）
     */
    public boolean uploadFile(MultipartFile file, String filePath) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            log.info("文件上传成功: {} -> {}", file.getOriginalFilename(), filePath);
            return true;
        } catch (Exception e) {
            log.error("文件上传失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 复制文件
     */
    public boolean copyFile(String sourceFilePath, String targetFilePath) {
        try {
            CopySource source = CopySource.builder()
                    .bucket(properties.getBucketName())
                    .object(sourceFilePath)
                    .build();

            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(targetFilePath)
                    .source(source)
                    .build());

            log.info("文件复制成功: {} -> {}", sourceFilePath, targetFilePath);
            return true;
        } catch (Exception e) {
            log.error("文件复制失败: {} -> {}", sourceFilePath, targetFilePath, e);
            return false;
        }
    }

    /**
     * 获取默认bucket名称
     */
    public String getBucketName() {
        return properties.getBucketName();
    }

    /**
     * 获取文件的永久访问URL（需要bucket为公开访问）
     */
    public String getPermanentUrl(String filePath) {
        return String.format("%s/%s/%s",
                properties.getEndpoint().replaceAll("/$", ""),
                properties.getBucketName(),
                filePath);
    }
}
