package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.common.constant.TaskStatus;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

/**
 * FileName: DocumentTask
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 21:30
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_document_task")
public class DocumentTask extends BaseEntity {

    private Long userId;

    private Long resumesId;

    private DocumentType documentType;

    private TaskStatus status = TaskStatus.PENDING;

    private String fileUrl;

    private String fileName;

    private Long fileSize;

    private String errorMessage;

    private Integer retryCount = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endAt;
}
