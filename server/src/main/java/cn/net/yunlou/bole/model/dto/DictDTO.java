package cn.net.yunlou.bole.model.dto;

import cn.net.yunlou.bole.common.BaseTreeDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: DictDTO Description: Created By laughtiger Created At 2025/11/30 22:06 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDTO extends BaseTreeDTO<DictDTO> {

    private String name;

    private String type;

    private String code;

    private String value;

    private String label;

    private Integer state;
}
