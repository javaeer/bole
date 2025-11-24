package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * FileName: BaseEntity
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 13:45
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseEntity implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(exist = false)
    private String keyWords;

    @TableField(exist = false)
    private String keyField;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:MM:SS", timezone = "GMT+8")
    private Date queryStartAt;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:MM:SS", timezone = "GMT+8")
    private Date queryStopAt;
}
