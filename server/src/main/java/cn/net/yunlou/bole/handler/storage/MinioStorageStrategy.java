package cn.net.yunlou.bole.handler.storage;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.StorageType;
import cn.net.yunlou.bole.common.utils.FileHashUtils;
import cn.net.yunlou.bole.common.utils.FileUploadUtils;
import cn.net.yunlou.bole.config.StorageMinioProperties;
import cn.net.yunlou.bole.handler.IStorageStrategy;
import cn.net.yunlou.bole.model.entity.File;
import io.minio.*;
import io.minio.http.Method;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "minio")
public class MinioStorageStrategy implements IStorageStrategy {

    private final StorageMinioProperties storageMinioProperties;

    private final MinioClient minioClient;

    @Autowired
    public MinioStorageStrategy(StorageMinioProperties storageMinioProperties) {
        this.storageMinioProperties = storageMinioProperties;

        // 创建MinIO客户端
        this.minioClient =
                MinioClient.builder()
                        .endpoint(storageMinioProperties.getEndpoint())
                        .credentials(
                                storageMinioProperties.getAccessKey(),
                                storageMinioProperties.getSecretKey())
                        .region(storageMinioProperties.getRegion())
                        .build();

        log.info("MinIO存储策略初始化完成，endpoint: {}", storageMinioProperties.getEndpoint());
        // 初始化时确保bucket存在
        ensureBucketExists(storageMinioProperties.getBucketName());
    }

    @Override
    public StorageType getType() {
        return StorageType.MINIO;
    }

    @Override
    public File store(MultipartFile multipartFile) {
        try {
            // 计算文件 唯一标识
            String fileKey = FileHashUtils.calculateFileHash(multipartFile);

            // 生成唯一文件名
            String originalFilename = multipartFile.getOriginalFilename();

            String uniqueFilename = generateUniqueFilename(originalFilename);

            // 生成路径+文件名
            String datePathFileName = generateDatePathFileName(uniqueFilename);

            // 上传到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(storageMinioProperties.getBucketName())
                            .object(datePathFileName)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build());

            log.info("文件上传成功: {} -> {}", originalFilename, datePathFileName);

            return File.builder()
                    .fileName(uniqueFilename)
                    .fileKey(fileKey)
                    .originalFilename(originalFilename)
                    .storagePath(datePathFileName)
                    .fileSizeBytes(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .storageType(StorageType.MINIO.getValue())
                    .accessUrl(getAccessUrl(datePathFileName))
                    .build();

        } catch (Exception e) {
            log.error("MinIO文件上传失败", e);
            throw new BusinessException(BusinessStatus.UNSUPPORTED_CONVERT_FILE_TYPE, "文件上传失败");
        }
    }

    @Override
    public File storeFile(java.io.File file, String fileName) {

        try {
            // 计算文件 唯一标识
            String fileKey = FileHashUtils.calculateHash(file);
            // 生成路径+文件名
            String datePathFileName = generateDatePathFileName(fileName);

            // 获取文件MIME类型
            String contentType = FileUploadUtils.getContentType(fileName);

            // 上传文件
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(storageMinioProperties.getBucketName())
                            .object(datePathFileName)
                            .filename(file.getAbsolutePath())
                            .contentType(contentType)
                            .build());

            return File.builder()
                    .fileName(fileName)
                    .fileKey(fileKey)
                    .originalFilename(file.getName())
                    .storagePath(datePathFileName)
                    .fileSizeBytes(file.length())
                    .contentType(contentType)
                    .storageType(StorageType.MINIO.getValue())
                    .accessUrl(getAccessUrl(datePathFileName))
                    .build();

        } catch (Exception e) {
            log.error("MinIO文件上传失败", e);
            throw new BusinessException(BusinessStatus.UNSUPPORTED_CONVERT_FILE_TYPE, "文件上传失败");
        }
    }

    @Override
    public File chunkStore(
            MultipartFile multipartFile,
            String fileName,
            Long fileCrc32,
            String fileExt,
            Integer chunk,
            Integer chunks) {
        return null;
    }

    @Override
    public boolean delete(String filePath) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(storageMinioProperties.getBucketName())
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
                            .bucket(storageMinioProperties.getBucketName())
                            .object(filePath)
                            .expiry(7 * 24 * 60 * 60) // 7天
                            .build());
        } catch (Exception e) {
            log.error("生成MinIO访问URL失败", e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "生成访问URL失败");
        }
    }

    /** 获取访问URL（可自定义过期时间） */
    public String getAccessUrl(String filePath, int duration, TimeUnit timeUnit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(storageMinioProperties.getBucketName())
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
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(storageMinioProperties.getBucketName())
                            .object(filePath)
                            .build());
        } catch (Exception e) {
            log.error("下载文件失败: {}", filePath, e);
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "文件下载失败");
        }
    }

    /** 下载文件为字节数组 */
    public ResponseEntity<byte[]> downloadAsBytes(String filePath) {
        try (InputStream inputStream = download(filePath);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            IOUtils.copy(inputStream, outputStream);
            byte[] bytes = outputStream.toByteArray();

            // 获取原始文件名
            String originalFilename = StringUtils.substringAfterLast(filePath, "/");

            HttpHeaders headers = new HttpHeaders();
            headers.add(
                    "Content-Disposition",
                    "attachment; filename=\""
                            + URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.name())
                            + "\"");
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
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(storageMinioProperties.getBucketName())
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

    /** 检查bucket是否存在，不存在则创建 */
    public boolean ensureBucketExists(String bucketName) {
        try {
            boolean exists =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' 创建成功", bucketName);
            }
            return true;
        } catch (Exception e) {
            log.error("创建或检查Bucket失败: {}", bucketName, e);
            return false;
        }
    }

    /** 创建存储bucket */
    public boolean createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket '{}' 创建成功", bucketName);
            return true;
        } catch (Exception e) {
            log.error("创建Bucket失败: {}", bucketName, e);
            return false;
        }
    }

    /** 删除存储bucket */
    public boolean deleteBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket '{}' 删除成功", bucketName);
            return true;
        } catch (Exception e) {
            log.error("删除Bucket失败: {}", bucketName, e);
            return false;
        }
    }

    /** 获取上传临时签名（用于前端直传） */
    public Map<String, String> generatePresignedPostFormData(
            String fileName, ZonedDateTime expirationTime) {
        try {
            PostPolicy postPolicy =
                    new PostPolicy(storageMinioProperties.getBucketName(), expirationTime);
            postPolicy.addEqualsCondition("key", fileName);

            Map<String, String> formData = minioClient.getPresignedPostFormData(postPolicy);

            // 转换key格式并添加host
            Map<String, String> result =
                    formData.entrySet().stream()
                            .collect(
                                    Collectors.toMap(
                                            entry -> entry.getKey().replace("-", ""),
                                            Map.Entry::getValue));

            result.put(
                    "host",
                    storageMinioProperties.getEndpoint()
                            + "/"
                            + storageMinioProperties.getBucketName());
            return result;
        } catch (Exception e) {
            log.error("生成上传临时签名失败: {}", fileName, e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "生成上传签名失败");
        }
    }

    /** 生成预签名URL（用于上传或下载） */
    public String generatePresignedUrl(
            String filePath, Method method, int duration, TimeUnit timeUnit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(storageMinioProperties.getBucketName())
                            .object(filePath)
                            .expiry(duration, timeUnit)
                            .build());
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", filePath, e);
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "生成预签名URL失败");
        }
    }

    /** 上传文件（直接上传方式） */
    public boolean uploadFile(MultipartFile file, String filePath) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(storageMinioProperties.getBucketName())
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

    /** 复制文件 */
    public boolean copyFile(String sourceFilePath, String targetFilePath) {
        try {
            CopySource source =
                    CopySource.builder()
                            .bucket(storageMinioProperties.getBucketName())
                            .object(sourceFilePath)
                            .build();

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(storageMinioProperties.getBucketName())
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

    /** 获取默认bucket名称 */
    public String getBucketName() {
        return storageMinioProperties.getBucketName();
    }

    /** 获取文件的永久访问URL（需要bucket为公开访问） */
    public String getPermanentUrl(String filePath) {
        return String.format(
                "%s/%s/%s",
                storageMinioProperties.getEndpoint().replaceAll("/$", ""),
                storageMinioProperties.getBucketName(),
                filePath);
    }
}
