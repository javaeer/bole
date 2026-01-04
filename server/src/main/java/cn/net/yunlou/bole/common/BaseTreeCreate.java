package cn.net.yunlou.bole.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static cn.net.yunlou.bole.common.BaseTreeEntity.ROOT_ID;

/**
 * FileName: BaseTreeCreate Description: Created By laughtiger Created At 2025/12/5 01:29 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseTreeCreate extends BaseCreate {
    private Long parentId = ROOT_ID;
}
