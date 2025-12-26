package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_msg_template")
public class MessageTemplate extends BaseEntity {

    public static final Long VALIDATE_CODE_ID = 1L;

    /** 主题 */
    private String subject;

    /** 中文模板 */
    private String template;

    /** 繁体中文模板 */
    private String tcTemplate;

    /** 英文内容模板 */
    private String enTemplate;

    /** 模板对应 APP下载地址或站点地址 */
    private String website;

    /** 是否需要校验 需要 则将校验 主要内容（具体的 content） 的准确性与有效时间 */
    private Boolean isVerify;

    /** 如 typeId 为1时，不可为空，表示 验证码的长度 */
    private Integer length;

    /** 有效时长 */
    private Integer duration;
}
