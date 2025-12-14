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
    SELF_EVALUATION("ResumeSelfEvaluation", "自我评价组件"),
    EDUCATION_EXPERIENCE("ResumeEducationExperience", "教育经历组件"),
    PROJECT_EXPERIENCE("ResumeProjectExperience", "项目经历组件"),
    COMPANY_EXPERIENCE("ResumeCompanyExperience", "公司经历组件"),
    WORK_EXPERIENCE("ResumeWorkExperience", "工作经历组件"),
    JOB_INTENTION("ResumeJobIntention", "求职意向组件"),
    USER_BASIC_INFO("ResumeUserBasicInfo", "用户基础信息组件");

    private final String value;

    private final String label;
}
