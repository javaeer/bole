package cn.net.yunlou.bole.common;

import lombok.*;

/**
 * FileName: BaseTreeQuery Description: Created By laughtiger Created At 2025/11/30 22:05 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseTreeQuery extends BaseQuery {

    private Long parentId;
}
