package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.*;

/**
 * FileName: File Description: Created By laughtiger Created At 2025/12/4 00:38 Modified By Modified
 * At
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_file")
public class File extends BaseEntity {

    private String fileName;

    private String fileKey; // 文件全局唯一标识（MD5/SHA256）

    private String fileSize; // 文件大小（带单位）

    private Long fileSizeBytes; // 文件大小（字节）

    private String originalFilename; // 原始文件名

    private String storagePath; // 存储路径（相对路径）

    private String storageType; // 存储类型：LOCAL, MINIO

     private String contentType; //类型

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime; // 最后访问时间

    private String accessUrl; // 可访问的URL
}
