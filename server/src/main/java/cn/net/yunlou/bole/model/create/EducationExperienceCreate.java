package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * FileName: EducationExperienceAddRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:43 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "新增教育经历请求")
public class EducationExperienceCreate extends BaseCreate {

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

    private List<String> achievements;

    private Integer sort;
}
