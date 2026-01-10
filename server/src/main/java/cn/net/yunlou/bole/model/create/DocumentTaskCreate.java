package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import cn.net.yunlou.bole.common.constant.DocumentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: DocumentTaskCreate
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 21:52
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentTaskCreate extends BaseCreate {

    private Long resumesId;

    private DocumentType documentType;
}
