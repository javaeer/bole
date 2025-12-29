package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: FeedbackTypes Description: Created By laughtiger Created At 2025/12/29 16:36 Modified
 * By Modified At
 */
@Getter
@AllArgsConstructor
public enum FeedbackTypes implements IEnum<String> {
    BUG("bug", "Bug反馈"),
    SUGGESTION("suggestion", "功能建议"),
    EXPERIENCE("experience", "体验问题"),
    OTHER("other", "其他");

    private final String value;

    private final String label;
}
