package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

/**
 * FileName: ProjectExperienceDTO Description: Created By MR. WANG Created At 2025/11/26 17:44
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectExperienceView extends BaseView {
    private String name;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String description;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> achievements;

    private Integer sort;
}
