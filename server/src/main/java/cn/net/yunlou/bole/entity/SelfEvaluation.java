package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.util.List;
import lombok.*;

/**
 * FileName: SelfEvaluation Description: Created By laughtiger Created At 2025/12/14 04:08 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_self_evaluation")
public class SelfEvaluation extends BaseEntity {

    private Long userId;

    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> keywords;
}
