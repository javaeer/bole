package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User extends BaseEntity {

    /** 当前所在企业 */
    private Long companyId;

    private String username;

    @JsonIgnore private String password;

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

    // 非数据库字段 - 用户角色
    @TableField(exist = false)
    private List<UserRole> userRoles;

    @TableField(exist = false)
    private List<GrantedAuthority> authorities;

    @TableField(exist = false)
    private Company company;
}
