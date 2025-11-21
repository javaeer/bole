package cn.net.yunlou.bole.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * FileName: UpdateUserRequest Description: Created By laughtiger Created At 2025/11/19 14:14
 * Modified By Modified At
 */
@Data
@Schema(description = "编辑用户请求")
public class UpdateUserRequest {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    private String password;

    private String email;

    private String phone;

    private String name;

    private String avatar;

    private String title;

    private String company;

    private String location;

    private String website;

    private String github;

    private String wechat;

    private String bio;
}
