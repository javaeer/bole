package cn.net.yunlou.bole.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * FileName: RegisterRequest Description: Created By laughtiger Created At 2025/11/19 14:23 Modified
 * By Modified At
 */
@Data
@Builder
public class RegisterRequest implements Serializable {

    private String username;

    private String email;

    private String area;

    private String phone;

    @NotBlank(message = "密码不得为空")
    private String password;


}
