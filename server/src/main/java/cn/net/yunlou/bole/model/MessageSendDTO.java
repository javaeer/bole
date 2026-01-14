package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: MessageSendDto Description: Created By laughtiger Created At 2025/12/25 01:54 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageSendDTO extends BaseDTO {

    /** 接收人 */
    private String to;

    private Long templateId;
}
