package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

/**
 * FileName: UserDTO Description: Created By MR. WANG Created At 2025/11/26 17:48 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserView extends BaseView {

    /** 当前所在企业 */
    private Long companyId;

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    private String phone;

    private String name;

    private String avatar;

    private String title;

    private String location;

    private String website;

    private String github;

    private String wechat;

    private String bio;

    private Integer followers;

    private Integer fans;

    private Integer likes;

    private Integer status;

    private LocalDateTime lastLoginAt;

    private Integer workYears;
}
