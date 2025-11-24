package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * FileName: EducationExperience
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 20:44
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_education_experience")
public class EducationExperience extends BaseEntity {

    private Long userId;

    private String school;

    private String major;

    private String degree;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer isHighest;

    private String description;

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> achievements;

    private Integer sort;

}
