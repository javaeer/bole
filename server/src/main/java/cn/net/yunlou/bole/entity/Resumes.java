package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import java.util.List;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_resumes")
public class Resumes extends BaseEntity {

    private Long userId;

    private String salaryCurrent;

    private String salaryExpectation;

    private String status;

    private Long templateId;

    private Integer viewCount;

    private Integer downloadCount;

    // 非数据库字段 - 关联用户信息 获取基础信息
    @TableField(exist = false)
    private User user;

    // 非数据库字段 - 教育经历
    @TableField(exist = false)
    private List<EducationExperience> educationExperiences;

    // 非数据库字段 - 工作经历
    @TableField(exist = false)
    private List<WorkExperience> workExperiences;

    // 非数据库字段 - 项目经验
    @TableField(exist = false)
    private List<ProjectExperience> projectExperiences;

    // 非数据库字段 - 技能
    @TableField(exist = false)
    private List<Skill> skills;
}
