package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import cn.net.yunlou.bole.entity.CompanyComment;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import java.util.List;

/**
 * FileName: CompanyDTO Description: Created By MR. WANG Created At 2025/11/26 17:42 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CompanyView extends BaseView {
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
    private List<CompanyCommentView> companyComments;
}
