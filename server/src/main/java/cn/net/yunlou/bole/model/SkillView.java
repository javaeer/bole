package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FileName: SkillDTO Description: Created By MR. WANG Created At 2025/11/26 17:47 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SkillView extends BaseView {
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
