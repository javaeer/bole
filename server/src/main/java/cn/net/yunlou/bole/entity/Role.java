package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * FileName: Role Description: Created By MR. WANG Created At 2025/11/19 14:01 Modified By Modified
 * At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_role")
public class Role extends BaseEntity {

    private String name;

    private String code;

    private String description;
}
