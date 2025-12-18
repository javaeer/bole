package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * FileName: ResumesComponentDefultConfig Description: Created By laughtiger Created At 2025/12/17
 * 17:11 Modified By Modified At
 */
@Data
public class ResumesComponentDefaultConfig implements Serializable {

    /** 传递给组件的属性，根据正式数据，对其中的默认值进行替换 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> props;

    /** 该组件独有的样式变量（映射到CSS变量或类名）不可变 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> styles;
}
