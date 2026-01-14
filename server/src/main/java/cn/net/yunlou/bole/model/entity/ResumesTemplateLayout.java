package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * {"columns": 2, "pageSize": "A4", "showPhoto": true, "components": ["UserBasicInfo",
 * "EducationExperience", "WorkExperience", "skills", "ProjectExperience"], "leftColumn":
 * ["personal_info", "skills", "languages"], "orientation": "portrait", "rightColumn": ["summary",
 * "work_experience", "projects", "education"], "showSidebar": true, "photoPosition": "left_top"}
 * {"margin": {"top": "15mm", "left": "15mm", "right": "15mm", "bottom": "15mm"}, "columns": 1,
 * "pageSize": "A4", "lineStyle": "solid", "showPhoto": false, "components": ["UserBasicInfo",
 * "EducationExperience", "WorkExperience", "skills", "ProjectExperience"], "orientation":
 * "portrait", "componentOrder": ["header", "personal_info", "professional_summary",
 * "work_experience", "education", "certifications", "skills"]} {"columns": 1, "bibStyle": "APA",
 * "pageSize": "A4", "showPhoto": false, "components": ["UserBasicInfo", "EducationExperience",
 * "WorkExperience", "Skills", "ProjectExperience"], "orientation": "portrait", "headerFooter":
 * true, "componentOrder": ["header", "UserBasicInfo", "EducationExperience", "WorkExperience",
 * "ProjectExperience", "Skills"], "showPageNumbers": true} FileName: ResumesTemplateLayout
 * Description: Created By laughtiger Created At 2025/12/15 16:32 Modified By Modified At
 */
@Data
public class ResumesTemplateLayout implements Serializable {
    private String type;

    private String gap;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesTemplateLayoutColumns columns;

    private String orientation;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> componentOrder;
}
