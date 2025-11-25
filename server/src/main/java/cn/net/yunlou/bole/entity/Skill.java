package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FileName: Skill
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 13:31
 * Modified By
 * Modified At
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_skill")
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

    private String tags;

    private Boolean isPublic;

    private Integer sort;

}
