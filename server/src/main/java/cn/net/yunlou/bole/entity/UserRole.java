package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.MultiEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * FileName: UserRole
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/19 13:32
 *
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_role")
public class UserRole extends MultiEntity<User, Role> {

    private Long userId;

    private Long roleId;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Role role;
}
