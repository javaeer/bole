package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

/**
 * FileName: WorkExperienceDTO Description: Created By MR. WANG Created At 2025/11/26 17:49 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WorkExperienceView extends BaseView {

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
