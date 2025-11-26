package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: CommentStatus
 * Description: 评论状态 枚举
 * Created By MR. WANG
 * Created At 2025/11/25 17:02
 * Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum CommentStatus implements IEnum<Integer> {

    PENDING(0, "待审核"),
    PUBLISHED(1, "已发布"),
    DELETED(2, "已删除");

    private final Integer value;

    private final String label;
}
