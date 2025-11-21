package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resumes")
public class Resume extends BaseEntity {

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "name")
    private String name;

    @TableField(value = "title")
    private String title;

    @TableField(value = "avatar")
    private String avatar;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "email")
    private String email;

    @TableField(value = "wechat")
    private String wechat;

    @TableField(value = "github")
    private String github;

    @TableField(value = "location")
    private String location;

    @TableField(value = "education")
    private String education;

    @TableField(value = "work_years")
    private Integer workYears;

    @TableField(value = "salary_expectation")
    private String salaryExpectation;

    @TableField(value = "status")
    private String status = "DRAFT";

    @TableField(value = "template_id")
    private Long templateId;

    @TableField(value = "view_count")
    private Integer viewCount = 0;

    @TableField(value = "download_count")
    private Integer downloadCount = 0;

    // 非数据库字段 - 关联用户信息
    @TableField(exist = false)
    private User user;

    // 非数据库字段 - 工作经历
    @TableField(exist = false)
    private List<WorkExperience> workExperiences;

    // 非数据库字段 - 项目经验
    @TableField(exist = false)
    private List<ProjectExperience> projectExperiences;

    // 非数据库字段 - 技能
    @TableField(exist = false)
    private List<Skill> skills;

    public enum ResumeStatus {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }
}
