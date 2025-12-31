package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.*;

/**
 * FileName: SelfEvaluation Description: Created By laughtiger Created At 2025/12/14 04:08 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_self_evaluation", autoResultMap = true, resultMap = "BaseResultMap")
public class SelfEvaluation extends BaseEntity {

    private Long userId;

    private String content;

    // 亮点
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> highlights;
}
