package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import cn.net.yunlou.bole.entity.*;
import java.util.List;
import lombok.*;

/**
 * FileName: ResumesDTO Description: Created By MR. WANG Created At 2025/11/26 17:45 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResumesView extends BaseView {
    private String salaryCurrent;

    private String salaryExpectation;

    private String status;

    private Long templateId;

    private Integer viewCount;

    private Integer downloadCount;

    // 非数据库字段 - 教育经历
    private List<EducationExperienceView> educationExperiences;

    // 非数据库字段 - 工作经历
    private List<WorkExperienceView> workExperiences;

    // 非数据库字段 - 项目经验
    private List<ProjectExperienceView> projectExperiences;

    // 非数据库字段 - 技能
    private List<SkillView> skills;
}
