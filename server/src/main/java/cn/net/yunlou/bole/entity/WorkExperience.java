package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("work_experiences")
public class WorkExperience extends BaseEntity {

    @TableField(value = "resume_id")
    private Long resumeId;

    @TableField(value = "company")
    private String company;

    @TableField(value = "position")
    private String position;

    @TableField(value = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @TableField(value = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @TableField(value = "is_current")
    private Boolean isCurrent = false;

    @TableField(value = "description")
    private String description;

    @TableField(
            value = "achievements",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> achievements;

    @TableField(value = "sort_order")
    private Integer sortOrder = 0;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted = 0;
}
