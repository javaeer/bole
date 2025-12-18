package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import java.util.Map;

/**
 * FileName: IComponentDataPopulatorStrategy Description: Created By laughtiger Created At
 * 2025/12/14 02:25 Modified By Modified At
 */
public interface IComponentDataPopulatorStrategy {

    /** 执行 组件数据填充 */
    Map<String, Object> populate(Long userId, Map<String, Object> templateProps);

    boolean supports(TemplateComponentKey componentType);
}
