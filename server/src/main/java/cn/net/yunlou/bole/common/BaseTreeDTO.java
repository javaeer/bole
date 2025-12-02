package cn.net.yunlou.bole.common;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: BaseTreeDTO Description: Created By laughtiger Created At 2025/11/30 22:06 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTreeDTO<D> extends BaseDTO {

    private Long parentId;

    private String path;

    private Integer level;

    private List<D> children;

    private D parent;
}
