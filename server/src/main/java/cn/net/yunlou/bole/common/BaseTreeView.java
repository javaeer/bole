package cn.net.yunlou.bole.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import lombok.*;

/**
 * FileName: BaseTreeDTO Description: Created By laughtiger Created At 2025/11/30 22:06 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "树状结构响应基类")
public class BaseTreeView<V> extends BaseView {

    private Long parentId;

    private String path;

    private Integer level;

    private List<V> children;

    private V parent;
}
