package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: WorkExperienceAddRequest Description: Created By MR. WANG Created At 2025/11/25 00:08
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "新增工作经历请求")
public class WorkExperienceCreate extends BaseCreate {
    private Long companyId;

    private String company;

    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Boolean isCurrent;

    private String description;

    private List<String> achievements;

    private Integer sort;
}
