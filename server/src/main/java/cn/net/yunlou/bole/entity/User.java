package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User extends BaseEntity {

    @TableField(value = "username")
    private String username;

    @TableField(value = "real_name")
    private String realName;

    @TableField(value = "password")
    private String password;

    @TableField(value = "email")
    private String email;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "name")
    private String name;

    @TableField(value = "avatar")
    private String avatar;

    @TableField(value = "title")
    private String title;

    @TableField(value = "company")
    private String company;

    @TableField(value = "location")
    private String location;

    @TableField(value = "website")
    private String website;

    @TableField(value = "github")
    private String github;

    @TableField(value = "wechat")
    private String wechat;

    @TableField(value = "bio")
    private String bio;

    @TableField(value = "followers")
    private Integer followers = 0;

    @TableField(value = "fans")
    private Integer fans = 0;

    @TableField(value = "likes")
    private Integer likes = 0;

    @TableField(value = "status")
    private String status = "ACTIVE";

    // 非数据库字段 - 用户角色
    @TableField(exist = false)
    private List<UserRole> userRoles;

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        BANNED
    }
}
