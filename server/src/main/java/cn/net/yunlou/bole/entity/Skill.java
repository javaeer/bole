package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: Skill
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 13:31
 * Modified By
 * Modified At
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_skill")
public class Skill extends BaseEntity {

    private Long userId;

}
