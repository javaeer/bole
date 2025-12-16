package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: ResumesTemplateComponents Description: Created By laughtiger Created At 2025/12/13
 * 22:57 Modified By Modified At
 */
@Getter
@AllArgsConstructor
public enum TemplateComponentType implements IEnum<String> {
    SELF_EVALUATION("SelfEvaluation", "自我评价组件"),
    EDUCATION_EXPERIENCE("EducationExperience", "教育经历组件"),
    PROJECT_EXPERIENCE("ProjectExperience", "项目经历组件"),
    WORK_EXPERIENCE("WorkExperience", "工作经历组件"),
    COMPANY_EXPERIENCE("CompanyExperience", "公司经历组件"),
    SKILLS("Skills", "技能专长组件"),
    JOB_INTENTION("JobIntention", "求职意向组件"),
    USER_BASIC_INFO("UserBasicInfo", "用户基础信息组件");

    private final String value;

    private final String label;
}
