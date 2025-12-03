package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: Banner Description: Created By laughtiger Created At 2025/11/28 11:10 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_system_banner")
public class Banner extends BaseEntity {

    private String name;

    private String image;

    private String path;

    private Integer sort;
}
