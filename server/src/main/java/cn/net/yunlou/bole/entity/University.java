package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * FileName: University
 * Description:
 * Created By laughtiger
 * Created At 2025/12/30 17:33
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_university")
public class University extends BaseEntity {

    private String name;

    private String code;

    /**
     * 985 211 重点 普通
     */
    private String type;

    private String holder;

    private String location;

    private String website;

    private String github;

    private String bio;

    private Integer followers;

    private Integer fans;

    private Integer likes;
}
