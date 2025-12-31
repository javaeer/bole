package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @Author javaeer(javaeer @ aliyun.com) @Date 2020/3/31 15:22 @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sms")
public class Sms extends BaseEntity {
    public static final String SMS_CACHE_KEY = "SMS:";

    private Integer state;

    // private String areaCode;

    private String phone;

    private String text;

    /** 核心内容 比如 验证码 */
    private String content;

    private Long templateId;

    @TableField(exist = false)
    private MessageTemplate template;
}
