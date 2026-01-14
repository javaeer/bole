package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: BindEmailDTO Description: Created By laughtiger Created At 2025/12/29 01:46 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "绑定换绑解绑邮箱")
public class BindEmailDTO extends BaseDTO {

    private String email;

    private String code;

    private String password;
}
