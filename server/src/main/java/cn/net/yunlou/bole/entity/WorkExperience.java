package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_work_experiences")
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
    private Boolean isCurrent;

    @TableField(value = "description")
    private String description;

    @TableField(
            value = "achievements",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> achievements;

    @TableField(value = "sort_order")
    private Integer sortOrder;
}
