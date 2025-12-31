package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

/**
 * FileName: EducationExperience Description: Created By MR. WANG Created At 2025/11/24 20:44
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_education_experience", autoResultMap = true, resultMap = "BaseResultMap")
public class EducationExperience extends BaseEntity {

    private Long userId;

    private Long universityId;

    private String university;

    private String major;

    private String degree;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer isHighest;

    private String description;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> achievements;

    private Integer sort;
}
