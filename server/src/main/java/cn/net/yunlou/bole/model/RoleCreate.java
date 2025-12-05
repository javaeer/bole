package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: RoleAddRequest Description: Created By MR. WANG Created At 2025/11/25 00:00 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "新增角色请求")
public class RoleCreate extends BaseCreate {

    private String name;

    private String code;
}
