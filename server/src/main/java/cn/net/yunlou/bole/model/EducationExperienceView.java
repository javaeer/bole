package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

/**
 * FileName: EducationExperienceDTO Description: Created By MR. WANG Created At 2025/11/26 17:44
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EducationExperienceView extends BaseView {
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
