package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ProjectExperienceAddRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:53 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "新增项目经历请求")
public class ProjectExperienceCreate extends BaseCreate {

    private String name;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String description;

    private List<String> achievements;

    private Integer sort;
}
