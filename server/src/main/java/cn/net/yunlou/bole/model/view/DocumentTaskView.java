package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.common.constant.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * FileName: DocumentTaskView
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 21:54
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentTaskView extends BaseView {

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
