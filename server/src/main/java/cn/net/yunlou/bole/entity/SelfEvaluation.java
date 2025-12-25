package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FileName: SelfEvaluation Description: Created By laughtiger Created At 2025/12/14 04:08 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_self_evaluation")
public class SelfEvaluation extends BaseEntity {

    private Long userId;

    private String content;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> keywords;
}
