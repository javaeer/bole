package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * FileName: ProjectExperience Description: Created By MR. WANG Created At 2025/11/19 13:31 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_project_experience", autoResultMap = true, resultMap = "BaseResultMap")
public class ProjectExperience extends BaseEntity {

    private Long userId;

    private String name;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String description;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> achievements;

    private Integer sort;
}
