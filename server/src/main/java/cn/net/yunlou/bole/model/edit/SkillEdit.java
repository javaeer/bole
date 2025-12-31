package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * FileName: SkillEditRequest Description: Created By MR. WANG Created At 2025/11/25 00:07 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "编辑技能请求")
public class SkillEdit extends BaseEdit {
    private String name;

    private String level;

    private String category;

    private String description;

    private Integer proficiencyPercent;

    private BigDecimal experienceYears;

    private Boolean isCertified;

    private String certificateName;

    private LocalDate certificateDate;

    private List<String> tags;

    private Boolean isPublic;

    private Integer sort;
}
