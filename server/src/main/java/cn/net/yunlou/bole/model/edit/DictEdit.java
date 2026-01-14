package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseTreeEdit;
import lombok.*;

/**
 * FileName: DictEditRequest Description: Created By laughtiger Created At 2025/11/30 22:13 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DictEdit extends BaseTreeEdit {
    private String name;

    private String type;

    private String code;

    private String value;

    private String label;

    private Integer state;
}
