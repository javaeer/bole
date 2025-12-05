package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * FileName: CompanyExperienceDTO Description: Created By MR. WANG Created At 2025/11/26 17:42
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CompanyExperienceView extends BaseView {
    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Boolean isCurrent;

    private String description;

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> website;

    private Integer sort;
}
