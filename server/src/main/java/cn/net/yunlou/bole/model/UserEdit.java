package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: UpdateUserRequest Description: Created By MR. WANG Created At 2025/11/19 14:14 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑用户请求")
public class UserEdit extends BaseEdit {

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "电子邮箱", example = "admin@yunlou.net.cn")
    private String email;

    @Schema(description = "手机号", example = "1888888888")
    private String phone;

    @Schema(description = "姓名", example = "大刀王五")
    private String name;

    @Schema(description = "头像", example = "admin.jpg")
    private String avatar;

    @Schema(description = "头衔", example = "CEO")
    private String title;

    @Schema(description = "企业", example = "中国云楼")
    private String company;

    @Schema(description = "坐标", example = "北京")
    private String location;

    @Schema(description = "网站", example = "yunlou.net.cn")
    private String website;

    @Schema(description = "GitHub", example = "admin@yunlou.net.cn")
    private String github;

    @Schema(description = "微信号", example = "yunlou")
    private String wechat;

    @Schema(description = "BIO", example = "admin")
    private String bio;
}
