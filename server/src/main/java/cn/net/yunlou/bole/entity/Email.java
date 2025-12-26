package cn.net.yunlou.bole.entity;

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
@TableName(value = "t_ems")
public class Email extends BaseEntity {

    public static final String EMAIL_CACHE_KEY = "EMAIL:";

    private String address;

    private String text;

    /** 核心内容 比如 验证码 */
    private String content;

    private Long templateId;

    private Integer state;

    @TableField(exist = false)
    private MessageTemplate template;
}
