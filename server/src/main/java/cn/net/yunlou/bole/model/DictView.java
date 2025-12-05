package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseTreeView;
import lombok.*;

/**
 * FileName: DictDTO Description: Created By laughtiger Created At 2025/11/30 22:06 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictView extends BaseTreeView<DictView> {

    private String name;

    private String type;

    private String code;

    private String value;

    private String label;

    private Integer state;
}
