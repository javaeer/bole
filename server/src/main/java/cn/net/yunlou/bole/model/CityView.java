package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseTreeView;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class CityView extends BaseTreeView<CityView> {
    private String name;

    private String enName;

    /** 首字母 */
    private String initial;

    /** 拼音 简称的拼音 全拼 小写 */
    private String pinyin;

    /** 简称 去掉所包含的以下汉字 区/新区 县 *族自治区/县 */
    private String shortName;
}
