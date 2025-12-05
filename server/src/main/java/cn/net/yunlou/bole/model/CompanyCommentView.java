package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import lombok.*;

/**
 * FileName: CompanyCommentDTO Description: Created By MR. WANG Created At 2025/11/26 00:57 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CompanyCommentView extends BaseView {
    /** 评论内容 */
    private String content;

    /** 评论人ID */
    private Long userId;

    /** 评论人姓名（冗余字段） */
    private String userName;

    /** 评论人头像（冗余字段） */
    private String userAvatar;

    /** 评分（1-5分） */
    private Integer score;

    /** 评论状态（0-待审核，1-已发布，2-已删除） */
    private Integer status;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复数 */
    private Integer replyCount;

    /** 是否匿名（0-否，1-是） */
    private Boolean anonymous;
}
