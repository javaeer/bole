package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * FileName: Skill Description: Created By MR. WANG Created At 2025/11/19 13:31 Modified By Modified
 * At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_skill",autoResultMap = true,resultMap = "BaseResultMap")
public class Skill extends BaseEntity {

    private Long userId;

    private String name;

    private String level;

    private String category;

    private String description;

    private Integer proficiencyPercent;

    private BigDecimal experienceYears;

    private Boolean isCertified;

    private String certificateName;

    private LocalDate certificateDate;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> tags;

    private Boolean isPublic;

    private Integer sort;
}
