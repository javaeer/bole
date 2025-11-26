package cn.net.yunlou.bole.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: BaseTreeEntity
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/25 17:00
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTreeEntity extends BaseEntity {

    private Long parentId = 0L;

    private String path;

    private Integer level = 1;
}
