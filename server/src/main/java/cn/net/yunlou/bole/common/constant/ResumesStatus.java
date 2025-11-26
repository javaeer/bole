package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResumesStatus implements IEnum<Integer> {
    DRAFT(1, "草稿"),
    PUBLISHED(2, "发布"),
    ARCHIVED(3, "存档");

    private final Integer value;

    private final String label;
}