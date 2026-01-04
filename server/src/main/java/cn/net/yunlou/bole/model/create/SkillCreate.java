package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

/**
 * FileName: SkillAddRequest Description: Created By MR. WANG Created At 2025/11/25 00:07 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "新增技能请求")
public class SkillCreate extends BaseCreate {
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
