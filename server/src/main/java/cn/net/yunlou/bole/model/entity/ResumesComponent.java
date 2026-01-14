package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * FileName: ResumesComponent Description: Created By laughtiger Created At 2025/12/17 15:32
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName(value = "t_resumes_component", autoResultMap = true)
public class ResumesComponent extends BaseEntity {

    private String name;

    /**
     * 对应预定义组件的名称
     *
     * @see TemplateComponentKey
     */
    private String key;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesComponentDefaultConfig defaultConfig;

    /** 为 数据更新做准备 */
}
