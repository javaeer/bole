package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.MultiEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FileName: Follow
 * Description:
 * Created By laughtiger
 * Created At 2025/12/24 19:34
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_follow_company")
public class FollowCompany extends MultiEntity {

    private Long userId;

    private Long companyId;

    @TableField(exist = false)
    private Company company;

    @TableField(exist = false)
    private User user;

}
