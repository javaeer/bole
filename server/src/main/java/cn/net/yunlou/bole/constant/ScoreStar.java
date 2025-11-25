package cn.net.yunlou.bole.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: ScoreStar
 * Description:
 * Created By laughtiger
 * Created At 2025/11/25 17:04
 * Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum ScoreStar implements IEnum<Integer> {

    ONE_STAR(1, "一星"),
    TWO_STARS(2, "二星"),
    THREE_STARS(3, "三星"),
    FOUR_STARS(4, "四星"),
    FIVE_STARS(5, "五星");

    private final Integer value;

    private final String label;
}
