package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * FileName: EducationExperienceEditRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:46 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "编辑教育经历请求")
public class EducationExperienceEdit extends BaseEdit {

    private String major;

    private String degree;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer isHighest;

    private String description;

    private List<String> achievements;

    private Integer sort;
}
