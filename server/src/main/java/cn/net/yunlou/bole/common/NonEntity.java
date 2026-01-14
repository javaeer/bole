package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

/**
 * FileName: NonEntity Description: 依附于 上层 实体主键 回填， 可谓无主键实体 一对一关系 Created By laughtiger Created At
 * 2025/12/13 00:03 Modified By Modified At
 */
@Data
public class NonEntity implements Serializable {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
}
