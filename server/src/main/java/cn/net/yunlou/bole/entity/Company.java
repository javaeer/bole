package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: Company Description: Created By MR. WANG Created At 2025/11/24 21:10 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_company")
public class Company extends BaseEntity {

    private String name;

    private String email;

    private String holder;

    private String location;

    private String website;

    private String github;

    private String wechat;

    private String bio;

    private Integer followers;

    private Integer fans;

    private Integer likes;

    /** 企业内部评论 */
    @TableField(exist = false)
    private List<CompanyComment> companyComments;
}
