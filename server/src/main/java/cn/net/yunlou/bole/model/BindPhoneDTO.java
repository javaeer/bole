package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: BindPhoneDTO Description: Created By laughtiger Created At 2025/12/29 01:43 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "绑定换绑解绑手机号")
public class BindPhoneDTO extends BaseDTO {

    private String phone;

    private String code;

    private String password;
}
