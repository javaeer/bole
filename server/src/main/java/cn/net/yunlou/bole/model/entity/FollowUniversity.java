package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.MultiEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * FileName: Follow Description: Created By laughtiger Created At 2025/12/24 19:34 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_follow_university")
public class FollowUniversity extends MultiEntity {

    private Long userId;

    private Long universityId;

    @TableField(exist = false)
    private University university;

    @TableField(exist = false)
    private User user;
}
