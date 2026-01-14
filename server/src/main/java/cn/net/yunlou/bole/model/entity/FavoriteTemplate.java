package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.MultiEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * FileName: CollectionTemplate Description: Created By laughtiger Created At 2025/12/24 19:56
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_favorite_template")
public class FavoriteTemplate extends MultiEntity {

    private Long userId;

    private Long templateId;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private ResumesTemplate template;
}
