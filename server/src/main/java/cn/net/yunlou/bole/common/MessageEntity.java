package cn.net.yunlou.bole.common;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * FileName: MessageEntity
 * Description:
 * Created By laughtiger
 * Created At 2025/12/24 23:56
 * Modified By
 * Modified At
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity implements Serializable {

    /**
     * 接收人
     */
    private String to;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容 模板需要先处理好
     */
    private String text;

}
