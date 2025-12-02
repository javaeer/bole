package cn.net.yunlou.bole.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: BaseTreeQuery Description: Created By laughtiger Created At 2025/11/30 22:05 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTreeQuery extends BaseQuery {

    private Long parentId;
}
