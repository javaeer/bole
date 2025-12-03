package cn.net.yunlou.bole.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

/**
 * FileName: System Description: Created By MR. WANG Created At 2025/11/24 21:10 Modified By
 * Modified At
 */
@Data
@TableName(value = "t_config")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Config implements Serializable {

    @TableId(type = IdType.INPUT)
    private String configKey;

    private String configValue;

    private String configDesc;

    private Long createdById;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Long updatedById;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private User createdBy;

    @TableField(exist = false)
    private User updatedBy;
}
