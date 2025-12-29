package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.*;

/**
 * FileName: Feedback Description: Created By laughtiger Created At 2025/12/29 16:34 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_feedback")
public class Feedback extends BaseEntity {

    private Long userId;

    private String type;

    private String content;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> images;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private Contact contact;
}
